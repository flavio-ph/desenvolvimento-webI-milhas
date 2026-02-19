package com.web.milhas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long expirationMillis;

    private SecretKey signingKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(Authentication authentication) {
        String username = authentication.getName();
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMillis);

        // Extrai as roles/authorities e junta em uma string separada por vírgulas
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); 

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(agora)
                .setExpiration(expiracao)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extrairUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validarToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        final String username = extrairUsername(token);
        return (username.equals(userDetails.getUsername()) && !estaExpirado(token));
    }

    private boolean estaExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
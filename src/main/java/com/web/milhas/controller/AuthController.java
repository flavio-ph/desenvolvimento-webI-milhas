package com.web.milhas.controller;

import com.web.milhas.dto.auth.AuthResponse;
import com.web.milhas.dto.auth.LoginRequestDTO;
import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.security.JwtTokenProvider;
import com.web.milhas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.senha())
        );

        String token = jwtTokenProvider.gerarToken(auth.getName());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@Valid @RequestBody RegisterRequest req) {
        authService.registrarUsuario(req);
    }
}
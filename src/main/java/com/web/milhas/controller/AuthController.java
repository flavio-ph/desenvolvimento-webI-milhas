package com.web.milhas.controller;

import com.web.milhas.dto.auth.AuthResponse;
import com.web.milhas.dto.auth.LoginRequestDTO;
import com.web.milhas.dto.auth.PasswordResetRequest;
import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.auth.UpdatePasswordRequest;
import com.web.milhas.security.JwtTokenProvider;
import com.web.milhas.service.UsuarioService; // <-- CORREÇÃO: Importa a interface
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
    private UsuarioService usuarioService;

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
        usuarioService.registrarUsuario(req);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody PasswordResetRequest req) {
        usuarioService.requestPasswordReset(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody UpdatePasswordRequest req) {
        usuarioService.resetPassword(req);
        return ResponseEntity.ok().build();
    }
}
package com.web.milhas.controller;

import com.web.milhas.dto.auth.AuthResponse;
import com.web.milhas.dto.auth.LoginRequestDTO;
import com.web.milhas.dto.auth.PasswordResetRequest;
import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.auth.UpdatePasswordRequest;
import com.web.milhas.dto.usuario.Verify2FARequestDTO;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.security.JwtTokenProvider;
import com.web.milhas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioService usuarioService;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        // 1. Busca o usuário pelo email fornecido no login
        UsuarioEntity usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Valida a senha
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // 3. Verifica se o 2FA está ativado
        if (usuario.isTwoFactorEnabled()) {
            // 4. Gera código de 6 dígitos
            String codigo2FA = String.format("%06d", new java.util.Random().nextInt(999999));

            // 5. Salva o código no banco para validação posterior (idealmente com data de expiração)
            usuario.setTwoFactorCode(codigo2FA);
            usuarioRepository.save(usuario);

            // 6. Envia o email usando o email associado à conta do usuário
            emailService.enviarCodigo2FA(usuario.getEmail(), codigo2FA);

            // Retorna resposta indicando que o frontend deve exibir a tela de inserção do código
            return ResponseEntity.ok().body("{\"require2FA\": true, \"message\": \"Código enviado para o email cadastrado\"}");
        }

        // Fluxo normal de login (geração de JWT) caso 2FA não esteja ativo
        String token = gerarJwt(usuario);
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@Valid @RequestBody RegisterRequest req) {
        usuarioService.registrarUsuario(req);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody PasswordResetRequest req) {
        usuarioService.requestPasswordReset(req.email());
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody UpdatePasswordRequest req) {
        usuarioService.resetPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@RequestBody Verify2FARequestDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getTwoFactorCode() == null || !usuario.getTwoFactorCode().equals(dto.codigo())) {
            throw new RuntimeException("Código inválido");
        }

        if (usuario.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado");
        }

        // Invalida o código após uso bem-sucedido
        usuario.setTwoFactorCode(null);
        usuario.setTwoFactorExpiry(null);
        usuarioRepository.save(usuario);

        // Gera e retorna o token JWT real
        String token = jwtService.gerarToken(usuario); // Ajuste para o seu serviço de JWT
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
}
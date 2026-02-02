package com.web.milhas.controller;

import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.dto.usuario.UsuarioUpdateRequest;
import com.web.milhas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(usuarioService.getProfile(userEmail));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioUpdateRequest dto) {

        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(usuarioService.updateProfile(userEmail, dto));
    }
    

    @GetMapping("/me/2fa/generate")
    public ResponseEntity<Void> generate2FA(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        String code = usuarioService.generateTwoFactorSetup(userDetails.getUsername());
        System.out.println("Código 2FA gerado: " + code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/2fa/verify")
    public ResponseEntity<Void> verify2FA(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestBody java.util.Map<String, Integer> payload) {
        
        int code = payload.get("code");
        boolean isValid = usuarioService.verifyTwoFactor(userDetails.getUsername(), code);
        
        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/me/2fa/disable")
    public ResponseEntity<Void> disable2FA(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/upload-foto")
    public ResponseEntity<Void> uploadFoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("foto") org.springframework.web.multipart.MultipartFile foto) {
        
        usuarioService.uploadFotoPerfil(userDetails.getUsername(), foto);
        return ResponseEntity.ok().build();
    }

}
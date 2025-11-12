package com.web.milhas.controller;

import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.dto.usuario.UsuarioUpdateRequest;
import com.web.milhas.service.impl.UsuarioServiceImpl;
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

    private final UsuarioServiceImpl usuarioServiceImpl;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
                String userEmail = userDetails.getUsername();
                return ResponseEntity.ok(usuarioServiceImpl.getProfile(userEmail));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioUpdateRequest dto) {

        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(usuarioServiceImpl.updateProfile(userEmail, dto));

    }


}

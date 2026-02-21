package com.web.milhas.service;

import org.springframework.web.multipart.MultipartFile;

import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.auth.UpdatePasswordRequest;
import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.dto.usuario.UsuarioUpdateRequest;

public interface UsuarioService {
    void registrarUsuario(RegisterRequest dto);

    void resetPassword(UpdatePasswordRequest dto);

    void uploadFotoPerfil(String email, MultipartFile arquivo);

    UsuarioResponse updateProfile(String userEmail, UsuarioUpdateRequest dto);

    UsuarioResponse getProfile(String userEmail);

    void requestPasswordReset(String email);

    String generateTwoFactorSetup(String email);

    boolean verifyTwoFactor(String email, int code);

    void disableTwoFactor(String email);
}
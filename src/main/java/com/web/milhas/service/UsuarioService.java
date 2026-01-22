package com.web.milhas.service;

import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.auth.UpdatePasswordRequest;
import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.dto.usuario.UsuarioUpdateRequest;

public interface UsuarioService {
    void registrarUsuario(RegisterRequest dto);
    UsuarioResponse updateProfile(String userEmail, UsuarioUpdateRequest dto);
    UsuarioResponse getProfile(String userEmail);


    String requestPasswordReset(String email);

    void resetPassword(UpdatePasswordRequest dto);

    String generateTwoFactorSetup(String email);
    boolean verifyTwoFactor(String email, int code);
}
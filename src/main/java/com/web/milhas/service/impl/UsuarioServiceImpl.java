package com.web.milhas.service.impl;

import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.dto.auth.UpdatePasswordRequest;
import com.web.milhas.dto.usuario.UsuarioResponse;
import com.web.milhas.dto.usuario.UsuarioUpdateRequest;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.UserRole;
import com.web.milhas.exception.EmailAlreadyExistsException;
import com.web.milhas.exception.InvalidPasswordException;
import com.web.milhas.exception.InvalidTokenException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.UsuarioMapper; // Novo import
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper; // Injeção do Mapper
    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Override
    @Transactional
    public void registrarUsuario(RegisterRequest dto) {
        if (usuarioRepository.findEntityByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyExistsException("O e-mail fornecido já está em uso.");
        }

        // Utilizando o Mapper para criar a entidade inicial
        UsuarioEntity usuario = usuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        if (dto.role() == null) {
            usuario.setRole(UserRole.USER);
        }

        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse updateProfile(String userEmail, UsuarioUpdateRequest dto) {
        UsuarioEntity usuario = findUsuarioByEmail(userEmail);

        if (dto.nome() != null) usuario.setNome(dto.nome());
        if (dto.telefone() != null) usuario.setTelefone(dto.telefone());
        if (dto.cpf() != null) usuario.setCpf(dto.cpf());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        if (dto.fotoPerfil() != null && !dto.fotoPerfil().isBlank()) {
            usuario.setFotoPerfil(dto.fotoPerfil());
        }

        UsuarioEntity updatedUsuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(updatedUsuario); // Conversão via Mapper
    }

    @Override
    public UsuarioResponse getProfile(String userEmail) {
        UsuarioEntity usuario = findUsuarioByEmail(userEmail);
        return usuarioMapper.toResponse(usuario); // Conversão via Mapper
    }

    @Override
    @Transactional
    public String requestPasswordReset(String email) {
        UsuarioEntity usuario = findUsuarioByEmail(email);

        String token = UUID.randomUUID().toString();
        usuario.setResetPasswordToken(token);
        usuario.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        log.info("Token de reset gerado para {}: {}", email, token);

        return token;
    }

    @Override
    @Transactional
    public void resetPassword(UpdatePasswordRequest dto) {
        if (!dto.novaSenha().equals(dto.confirmacaoSenha())) {
            throw new InvalidPasswordException("As senhas não conferem.");
        }
        UsuarioEntity usuario = usuarioRepository.findByResetPasswordToken(dto.token())
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou não encontrado."));

        if (usuario.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expirado.");
        }
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuario.setResetPasswordToken(null);
        usuario.setResetPasswordTokenExpiry(null);
        usuarioRepository.save(usuario);
    }

    private UsuarioEntity findUsuarioByEmail(String email) {
        return usuarioRepository.findEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    @Override
    @Transactional
    public String generateTwoFactorSetup(String email) {
        UsuarioEntity usuario = findUsuarioByEmail(email);

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        usuario.setVerificationCode(code);
        usuario.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(5));
        usuarioRepository.save(usuario);

        return code;
    }

    @Override
    @Transactional
    public boolean verifyTwoFactor(String email, int codeInput) {
        UsuarioEntity usuario = findUsuarioByEmail(email);
        String codeStr = String.valueOf(codeInput);

        if (usuario.getVerificationCode() == null || usuario.getVerificationCodeExpiry() == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(usuario.getVerificationCodeExpiry())) {
            return false;
        }

        if (usuario.getVerificationCode().equals(codeStr)) {
            usuario.setTwoFactorEnabled(true);
            usuario.setVerificationCode(null);
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void uploadFotoPerfil(String email, MultipartFile arquivo) {
        UsuarioEntity usuario = findUsuarioByEmail(email);

        String nomeOriginal = StringUtils.cleanPath(arquivo.getOriginalFilename());
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf('.'));
        String nomeUnico = UUID.randomUUID().toString() + extensao;

        try {
            Path targetLocation = Paths.get("./uploads").toAbsolutePath().normalize().resolve(nomeUnico);
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            usuario.setFotoPerfil(nomeUnico);
            usuarioRepository.save(usuario);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao salvar foto de perfil", ex);
        }
    }
}
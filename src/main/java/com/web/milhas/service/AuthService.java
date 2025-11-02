package com.web.milhas.service;

import com.web.milhas.dto.auth.RegisterRequest;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.EmailAlreadyExistsException;
import com.web.milhas.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrarUsuario(RegisterRequest dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyExistsException("O e-mail fornecido já está em uso.");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        usuarioRepository.save(usuario);

    }
}
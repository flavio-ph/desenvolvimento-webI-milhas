package com.web.milhas.repository;

import com.web.milhas.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    // Método essencial para o UserDetailsServiceImpl carregar o usuário
    Optional<UserDetails> findByEmail(String email);

    Optional<UsuarioEntity> findEntityByEmail(String email);
}
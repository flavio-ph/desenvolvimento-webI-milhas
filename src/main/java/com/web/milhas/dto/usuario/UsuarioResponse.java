package com.web.milhas.dto.usuario;

import com.web.milhas.entity.enums.UserRole;

public record UsuarioResponse(
    Long id, 
    String nome, 
    String email, 
    String telefone, 
    String cpf,      
    UserRole role    
) {}
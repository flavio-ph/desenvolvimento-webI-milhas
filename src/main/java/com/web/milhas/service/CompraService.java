package com.web.milhas.service;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CompraService {
    CompraResponse registrarCompra(CompraRequest dto, String emailUsuario);

    ResumoPendentesDTO calcularResumoPendentes(String emailUsuario);

    void creditarCompra(Long compraId);

    void creditarCompra(Long compraId, String emailUsuario);

    Resource baixarComprovante(Long compraId, String emailUsuario);

    org.springframework.data.domain.Page<CompraResponse> listarCompras(String username, Long cartaoId, Pageable pageable);}

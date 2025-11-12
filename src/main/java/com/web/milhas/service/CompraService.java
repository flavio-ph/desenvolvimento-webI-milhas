package com.web.milhas.service;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;

public interface CompraService {
    CompraResponse registrarCompra(CompraRequest dto, String emailUsuario);
}

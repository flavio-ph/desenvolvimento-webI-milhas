package com.web.milhas.service;

import com.web.milhas.dto.saldo.SaldoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import java.util.List;

public interface SaldoService {


    List<SaldoPontosResponse> consultarSaldos(String emailUsuario);

    void creditarPontosCompra(CompraEntity compra);
}
package com.web.milhas.service;

import com.web.milhas.dto.promocao.PromocaoRequest;
import com.web.milhas.dto.promocao.PromocaoResponse;
import java.util.List;

public interface PromocaoService {

    List<PromocaoResponse> listarAtivas();

    PromocaoResponse criarPromocao(PromocaoRequest dto);
}
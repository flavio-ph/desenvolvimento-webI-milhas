package com.web.milhas.service;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import java.util.List;

public interface BandeiraService {

    List<BandeiraDTO> listarTodas();

    BandeiraDTO salvar(BandeiraDTO dto);

    void deletar(Long id);
}
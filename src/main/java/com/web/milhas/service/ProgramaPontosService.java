package com.web.milhas.service;

import com.web.milhas.dto.programapontos.ProgramaPontosDTO;
import java.util.List;

public interface ProgramaPontosService {

    List<ProgramaPontosDTO> listarTodos();


    ProgramaPontosDTO salvar(ProgramaPontosDTO dto);

    void deletar(Long id);
}
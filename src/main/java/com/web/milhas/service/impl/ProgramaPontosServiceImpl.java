package com.web.milhas.service.impl;

import com.web.milhas.dto.programapontos.ProgramaPontosDTO;
import com.web.milhas.entity.ProgamaPontosEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.ProgramaPontosRepository;
import com.web.milhas.service.ProgramaPontosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramaPontosServiceImpl implements ProgramaPontosService {

    private final ProgramaPontosRepository programaPontosRepository;

    @Override
    public List<ProgramaPontosDTO> listarTodos() {
        return programaPontosRepository.findAll().stream()
                .map(p -> new ProgramaPontosDTO(p.getId(), p.getNome()))
                .toList();
    }

    @Override
    @Transactional
    public ProgramaPontosDTO salvar(ProgramaPontosDTO dto) {
        ProgamaPontosEntity entity = new ProgamaPontosEntity();
        if (dto.id() != null) {
            entity = programaPontosRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));
        }
        entity.setNome(dto.nome());
        ProgamaPontosEntity salvo = programaPontosRepository.save(entity);
        return new ProgramaPontosDTO(salvo.getId(), salvo.getNome());
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!programaPontosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Programa de pontos não encontrado.");
        }
        programaPontosRepository.deleteById(id);
    }
}
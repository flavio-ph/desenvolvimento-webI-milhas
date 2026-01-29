package com.web.milhas.service.impl;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.BandeiraRepository;
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.service.BandeiraService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandeiraServiceImpl implements BandeiraService {

    private final BandeiraRepository bandeiraRepository;
    private final CartaoRepository cartaoRepository;

    @Override
    public List<BandeiraDTO> listarTodas() {
        return bandeiraRepository.findAll()
                .stream()
                .map(this::toDTOComContagem) 
                .toList();
    }

    @Override
    public List<BandeiraDTO> listarAtivas() {
        return bandeiraRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BandeiraDTO salvar(BandeiraDTO dto) {
        BandeiraEntity entity = toEntity(dto);
        
        if (entity.getStatus() == null || entity.getStatus().isEmpty()) {
            entity.setStatus("ACTIVE");
        }
        if (entity.getCor() == null || entity.getCor().isEmpty()) {
            entity.setCor("bg-slate-900");
        }

        BandeiraEntity salvo = bandeiraRepository.save(entity);
        return toDTO(salvo);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (bandeiraRepository.existsById(id)) {
            bandeiraRepository.deleteById(id);
        } else {
            throw new RuntimeException("Bandeira não encontrada para o ID: " + id);
        }
    }

    private BandeiraDTO toDTOComContagem(BandeiraEntity entity) {
        long quantidade = cartaoRepository.countByBandeira(entity);
        
        return new BandeiraDTO(
                entity.getId(),
                entity.getNome(),
                entity.getStatus(),
                entity.getCor(),
                quantidade 
        );
    }

    private BandeiraDTO toDTO(BandeiraEntity entity) {
        return new BandeiraDTO(entity.getId(), entity.getNome(), entity.getStatus(), entity.getCor(), 0L);
    }

    private BandeiraEntity toEntity(BandeiraDTO dto) {
        BandeiraEntity entity = new BandeiraEntity();
        entity.setId(dto.id());
        entity.setNome(dto.nome());
        entity.setStatus(dto.status());
        entity.setCor(dto.cor());
        return entity;
    }
}
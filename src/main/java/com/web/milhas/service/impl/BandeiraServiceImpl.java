package com.web.milhas.service.impl;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.mapper.BandeiraMapper; // Novo import
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
    private final BandeiraMapper bandeiraMapper; // Injeção do Mapper

    @Override
    public List<BandeiraDTO> listarTodas() {
        return bandeiraRepository.findAll()
                .stream()
                .map(entity -> {
                    BandeiraDTO dto = bandeiraMapper.toDTO(entity);
                    return new BandeiraDTO(
                            dto.id(),
                            dto.nome(),
                            dto.status(),
                            dto.cor(),
                            cartaoRepository.countByBandeira(entity)
                    );
                })
                .toList();
    }

    @Override
    public List<BandeiraDTO> listarAtivas() {
        return bandeiraRepository.findByStatus("ACTIVE")
                .stream()
                .map(bandeiraMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BandeiraDTO salvar(BandeiraDTO dto) {
        BandeiraEntity entity = bandeiraMapper.toEntity(dto);

        if (entity.getStatus() == null || entity.getStatus().isEmpty()) {
            entity.setStatus("ACTIVE");
        }
        if (entity.getCor() == null || entity.getCor().isEmpty()) {
            entity.setCor("bg-slate-900");
        }

        BandeiraEntity salvo = bandeiraRepository.save(entity);
        return bandeiraMapper.toDTO(salvo);
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

}
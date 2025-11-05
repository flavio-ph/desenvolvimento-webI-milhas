package com.web.milhas.service;


import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.BandeiraRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandeiraService {

    private final BandeiraRepository bandeiraRepository;

    public List<BandeiraDTO> listarTodas() {
        return bandeiraRepository.findAll().stream()
                .map(b -> new BandeiraDTO(b.getId(), b.getNome()))
                .toList();
    }

    @Transactional
    public BandeiraDTO salvar(BandeiraDTO dto) {
        BandeiraEntity bandeiraEntity = new BandeiraEntity();
        if (dto.id() != null) {
            bandeiraEntity = bandeiraRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Bandeira não encontrada."));
        }
        bandeiraEntity.setNome(dto.nome());
        BandeiraEntity salva = bandeiraRepository.save(bandeiraEntity);
        return new BandeiraDTO(salva.getId(), salva.getNome());
    }

    @Transactional
    public void deletar(Long id) {
        if (!bandeiraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bandeira não encontrada.");
        }
        bandeiraRepository.deleteById(id);
    }
}


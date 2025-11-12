package com.web.milhas.service.impl;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.BandeiraRepository;
import com.web.milhas.service.BandeiraService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandeiraServiceImpl implements BandeiraService {

    private final BandeiraRepository bandeiraRepository;

    @Override
    public List<BandeiraDTO> listarTodas() {
        return bandeiraRepository.findAll().stream()
                .map(b -> new BandeiraDTO(b.getId(), b.getNome()))
                .toList();
    }

    @Override
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

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!bandeiraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bandeira não encontrada.");
        }
        bandeiraRepository.deleteById(id);
    }
}
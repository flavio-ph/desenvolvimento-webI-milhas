package com.web.milhas.service;

import com.web.milhas.dto.promocao.PromocaoRequest;
import com.web.milhas.dto.promocao.PromocaoResponse;
import com.web.milhas.entity.ProgamaPontosEntity;
import com.web.milhas.entity.PromocaoEntity;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.ProgramaPontosRepository;
import com.web.milhas.repository.PromocaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProgramaPontosRepository programaPontosRepository;
    private final NotificacaoService notificacaoService;

    public List<PromocaoResponse> listarAtivas() {
        return promocaoRepository.findAll().stream() // Implementar filtrar por data aqui
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public PromocaoResponse criarPromocao(PromocaoRequest dto) {
        ProgamaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        PromocaoEntity promocao = new PromocaoEntity();
        promocao.setTitulo(dto.titulo());
        promocao.setDescricao(dto.descricao());
        promocao.setUrlPromocao(dto.urlPromocao());
        promocao.setDataInicio(dto.dataInicio());
        promocao.setDataFim(dto.dataFim());
        promocao.setProgramaPontos(programa);

        PromocaoEntity salva = promocaoRepository.save(promocao);

        notificacaoService.notificarTodos(
                "Nova promoção no programa " + programa.getNome() + ": " + dto.titulo(),
                TipoNotificacao.PROMOCAO
        );

        return mapToDTO(salva);
    }

    private PromocaoResponse mapToDTO(PromocaoEntity entity) {
        return new PromocaoResponse(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getUrlPromocao(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getProgramaPontos().getNome()
        );
    }
}
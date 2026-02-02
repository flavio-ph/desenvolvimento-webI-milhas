package com.web.milhas.service.impl;

import com.web.milhas.dto.promocao.PromocaoRequest;
import com.web.milhas.dto.promocao.PromocaoResponse;
import com.web.milhas.entity.ParticipacaoPromocaoEntity;
import com.web.milhas.entity.ProgramaPontosEntity;
import com.web.milhas.entity.PromocaoEntity;
import com.web.milhas.entity.enums.TipoNotificacao;
import com.web.milhas.exception.RegraNegocioException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.*;
import com.web.milhas.service.NotificacaoService;
import com.web.milhas.service.PromocaoService; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocaoServiceImpl implements PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProgramaPontosRepository programaPontosRepository;
    private final NotificacaoService notificacaoService;
    private final ParticipacaoPromocaoRepository participacaoRepository;
    private final UsuarioRepository usuarioRepository; 


    @Override
    public List<PromocaoResponse> listarAtivas() {

        return promocaoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public PromocaoResponse criarPromocao(PromocaoRequest dto) {

        if (dto.dataInicio().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("A data de início não pode ser no passado.");
        }

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data final deve ser posterior à data de início.");
        }

        ProgramaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        PromocaoEntity promocao = new PromocaoEntity();
        promocao.setTitulo(dto.titulo());
        promocao.setDescricao(dto.descricao());
        promocao.setUrlPromocao(dto.urlPromocao());
        promocao.setBonusPorcentagem(dto.bonusPorcentagem());
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
                entity.getBonusPorcentagem(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getProgramaPontos().getNome(),
                entity.getProgramaPontos().getId()
        );
    }

    @Override
    @Transactional
    public void deletarPromocao(Long id) {
        PromocaoEntity promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada."));
        
        promocaoRepository.delete(promocao);
    }

    @Override
    @Transactional
    public PromocaoResponse atualizarPromocao(Long id, PromocaoRequest dto) {
        PromocaoEntity promocao = promocaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada."));

        if (dto.dataFim().isBefore(dto.dataInicio())) {
            throw new IllegalArgumentException("A data final deve ser posterior à data de início.");
        }

        ProgramaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        promocao.setTitulo(dto.titulo());
        promocao.setDescricao(dto.descricao());
        promocao.setUrlPromocao(dto.urlPromocao());
        promocao.setBonusPorcentagem(dto.bonusPorcentagem());
        promocao.setDataInicio(dto.dataInicio());
        promocao.setDataFim(dto.dataFim());
        promocao.setProgramaPontos(programa);

        return mapToDTO(promocaoRepository.save(promocao));
    }


    @Override
    public void participarPromocao(Long idPromocao, String emailUsuario) {
        var usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        var promocao = promocaoRepository.findById(idPromocao)
                .orElseThrow(() -> new ResourceNotFoundException("Promoção não encontrada"));

        if (participacaoRepository.existsByUsuarioIdAndPromocaoId(usuario.getId(), promocao.getId())) {
            throw new RegraNegocioException("Você já está participando desta promoção.");
        }

        ParticipacaoPromocaoEntity part = new ParticipacaoPromocaoEntity();
        part.setUsuario(usuario);
        part.setPromocao(promocao);

        participacaoRepository.save(part);
    }

}
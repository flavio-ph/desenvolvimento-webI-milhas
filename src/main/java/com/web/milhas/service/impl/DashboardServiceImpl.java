package com.web.milhas.service.impl;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;
import com.web.milhas.dto.dashboard.PontosPorCartaoDTO;
import com.web.milhas.dto.dashboard.PrazoMedioRecebimentoDTO;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.MovimentacaoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final MovimentacaoPontosRepository movimentacaoRepository;

    public DashboardResponseDTO getDashboardData(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        List<PontosPorCartaoDTO> pontosPorCartao =
                compraRepository.findPontosAgrupadosPorCartao(usuario.getId());

        Double diasMedios =
                movimentacaoRepository.findPrazoMedioRecebimento(usuario.getId());
        PrazoMedioRecebimentoDTO prazoMedio = new PrazoMedioRecebimentoDTO(diasMedios);

        return new DashboardResponseDTO(pontosPorCartao, prazoMedio);
    }

}
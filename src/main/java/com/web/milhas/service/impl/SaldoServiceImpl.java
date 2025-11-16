package com.web.milhas.service.impl;

import com.web.milhas.dto.saldo.SaldoPontosResponse;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.ProgramaPontosEntity;
import com.web.milhas.entity.SaldoPontosEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.TipoMovimentacao;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.SaldoPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.MovimentacaoService;
import com.web.milhas.service.SaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaldoServiceImpl implements SaldoService {

    private final SaldoPontosRepository saldoPontosRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoService movimentacaoService;

    @Override
    public List<SaldoPontosResponse> consultarSaldos(String emailUsuario) {
        UsuarioEntity usuario = findUsuarioByEmail(emailUsuario);

        return saldoPontosRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public void creditarPontosCompra(CompraEntity compra) {
        if (compra.getPontosCalculados() == null || compra.getPontosCalculados().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        UsuarioEntity usuario = compra.getCartao().getUsuario();
        ProgramaPontosEntity programa = compra.getCartao().getProgramaPontos();

        SaldoPontosEntity saldo = saldoPontosRepository.findByUsuarioIdAndProgramaPontosId(usuario.getId(), programa.getId())
                .orElseGet(() -> criarNovoSaldo(usuario, programa));

        saldo.setTotalPontos(saldo.getTotalPontos().add(compra.getPontosCalculados()));
        SaldoPontosEntity saldoSalvo = saldoPontosRepository.save(saldo);

        movimentacaoService.registrarMovimentacao(
                saldoSalvo,
                TipoMovimentacao.ACUMULO,
                compra.getPontosCalculados(),
                "Crédito da compra: " + compra.getDescricao(),
                compra
        );
    }

    private SaldoPontosEntity criarNovoSaldo(UsuarioEntity usuario, ProgramaPontosEntity programa) {
        SaldoPontosEntity novoSaldo = new SaldoPontosEntity();
        novoSaldo.setUsuario(usuario);
        novoSaldo.setProgramaPontos(programa);
        novoSaldo.setTotalPontos(BigDecimal.ZERO);
        return novoSaldo;
    }

    private UsuarioEntity findUsuarioByEmail(String email) {
        return usuarioRepository.findEntityByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private SaldoPontosResponse mapToDTO(SaldoPontosEntity entity) {
        return new SaldoPontosResponse(
                entity.getId(),
                entity.getProgramaPontos().getNome(),
                entity.getTotalPontos()
        );
    }
}
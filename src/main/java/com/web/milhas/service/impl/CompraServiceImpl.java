package com.web.milhas.service.impl;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.exception.RegraNegocioException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.CompraMapper; // Novo import
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.CompraService;
import com.web.milhas.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final MovimentacaoService movimentacaoService;
    private final CartaoRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraMapper compraMapper;

    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (dto.valorGasto() == null || dto.valorGasto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da compra deve ser maior que zero.");
        }

        CartaoEntity cartao = cartaoRepository.findById(dto.cartaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Cartão não pertence ao usuário.");
        }

        LocalDate dataAtual = LocalDate.now();
        BigDecimal pontosCalculados = dto.valorGasto().multiply(cartao.getFatorConversao());

        CompraEntity compra = new CompraEntity();
        compra.setDescricao(dto.descricao());
        compra.setValorGasto(dto.valorGasto());
        compra.setDataCompra(dataAtual);
        compra.setCartao(cartao);
        compra.setPontosCalculados(pontosCalculados);
        compra.setDataCreditoPrevista(dataAtual);
        compra.setStatus(StatusCompra.PENDENTE);

        CompraEntity compraSalva = compraRepository.save(compra);

        return compraMapper.toResponse(compraSalva);
    }

    @Override
    public ResumoPendentesDTO calcularResumoPendentes(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        BigDecimal total = compraRepository.somarPontosPorStatus(usuario.getId(), StatusCompra.PENDENTE);
        LocalDate proximaData = compraRepository.findProximaDataCredito(usuario.getId(), StatusCompra.PENDENTE);

        Integer diasRestantes = null;
        if (proximaData != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), proximaData);
            diasRestantes = (int) Math.max(0, dias);
        }

        return new ResumoPendentesDTO(total, diasRestantes);
    }

    @Override
    @Transactional
    public void creditarCompra(Long compraId) {
        CompraEntity compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada"));

        if (compra.getStatus() == StatusCompra.CREDITADO) {
            throw new IllegalStateException("Esta compra já foi creditada.");
        }

        compra.setStatus(StatusCompra.CREDITADO);
        compraRepository.save(compra);
        movimentacaoService.gerarCreditoCompra(compra);
    }

    @Override
    @Transactional
    public void creditarCompra(Long compraId, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        CompraEntity compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada."));

        if (!compra.getCartao().getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Compra não pertence ao usuário.");
        }

        if (compra.getStatus() == StatusCompra.CREDITADO) {
            throw new IllegalStateException("Esta compra já foi creditada.");
        }

        compra.setStatus(StatusCompra.CREDITADO);
        compraRepository.save(compra);
        movimentacaoService.gerarCreditoCompra(compra);
    }
}
package com.web.milhas.service.impl;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.CompraService;
import com.web.milhas.service.MovimentacaoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final MovimentacaoService movimentacaoService;
    private final CartaoRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private static final int PRAZO_CREDITO_DIAS = 30;

    @Override
    @Transactional
    public CompraResponse registrarCompra(CompraRequest dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        CartaoEntity cartao = cartaoRepository.findById(dto.cartaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Cartão não pertence ao usuário.");
        }

        BigDecimal pontosCalculados = dto.valorGasto().multiply(cartao.getFatorConversao());

        LocalDate dataAtual = LocalDate.now();
        LocalDate dataCreditoPrevista = dataAtual.plusDays(PRAZO_CREDITO_DIAS);

        CompraEntity compra = new CompraEntity();
        compra.setDescricao(dto.descricao());
        compra.setValorGasto(dto.valorGasto());

        compra.setDataCompra(dataAtual);

        compra.setCartao(cartao);
        compra.setPontosCalculados(pontosCalculados);
        compra.setDataCreditoPrevista(dataCreditoPrevista);
        compra.setStatus(StatusCompra.PENDENTE);

        CompraEntity compraSalva = compraRepository.save(compra);
        return mapToDTO(compraSalva);
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

    // desenvolvimento-webI-milhas/src/main/java/com/web/milhas/service/impl/CompraServiceImpl.java

    @Override
    @Transactional // Garante que se o crédito falhar, o status da compra não mude
    public void creditarCompra(Long compraId) {
        // 1. Localiza a compra
        CompraEntity compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada"));

        // 2. Verifica se já não foi processada
        if (compra.getStatus() == StatusCompra.CREDITADO) {
            throw new IllegalStateException("Esta compra já foi creditada.");
        }

        // 3. Atualiza o status para CREDITADA
        compra.setStatus(StatusCompra.CREDITADO);
        compraRepository.save(compra);

        // 4. GATILHO: Chama o serviço de movimentação para gerar os pontos no extrato
        movimentacaoService.gerarCreditoCompra(compra);
    }

    private CompraResponse mapToDTO(CompraEntity entity) {
        Integer diasParaCredito = null;

        if (entity.getStatus() == StatusCompra.PENDENTE) {
            LocalDate hoje = LocalDate.now();
            LocalDate dataPrevista = entity.getDataCreditoPrevista();

            if (dataPrevista != null) {
                long dias = ChronoUnit.DAYS.between(hoje, dataPrevista);
                diasParaCredito = (int) Math.max(0, dias);
            }
        }

        return new CompraResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValorGasto(),
                entity.getPontosCalculados(),
                entity.getDataCompra(),
                entity.getDataCreditoPrevista(),
                entity.getStatus(),
                entity.getCartao().getId(),
                entity.getCartao().getNomePersonalizado(),
                diasParaCredito
        );
    }
}
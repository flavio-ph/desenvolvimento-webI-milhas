package com.web.milhas.service.impl;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
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
        LocalDate dataCreditoPrevista = dto.dataCompra().plusDays(PRAZO_CREDITO_DIAS);

        CompraEntity compra = new CompraEntity();
        compra.setDescricao(dto.descricao());
        compra.setValorGasto(dto.valorGasto());
        compra.setDataCompra(dto.dataCompra());
        compra.setCartao(cartao);
        compra.setPontosCalculados(pontosCalculados);
        compra.setDataCreditoPrevista(dataCreditoPrevista);
        compra.setStatus(StatusCompra.PENDENTE);

        CompraEntity compraSalva = compraRepository.save(compra);
        return mapToDTO(compraSalva);
    }

    private CompraResponse mapToDTO(CompraEntity entity) {
        return new CompraResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValorGasto(),
                entity.getPontosCalculados(),
                entity.getDataCompra(),
                entity.getDataCreditoPrevista(),
                entity.getStatus(),
                entity.getCartao().getId(),
                entity.getCartao().getNomePersonalizado()
        );
    }
}
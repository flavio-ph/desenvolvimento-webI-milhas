package com.web.milhas.service.impl;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.CompraEntity;
import com.web.milhas.entity.ComprovanteCompraEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.entity.enums.StatusCompra;
import com.web.milhas.exception.RegraNegocioException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.CompraMapper; // Novo import
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.repository.CompraRepository;
import com.web.milhas.repository.ComprovanteCompraRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.CompraService;
import com.web.milhas.service.MovimentacaoService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final MovimentacaoService movimentacaoService;
    private final CartaoRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompraMapper compraMapper;
    private final ComprovanteCompraRepository comprovanteCompraRepository;

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

    @Override
    public Resource baixarComprovante(Long compraId, String emailUsuario) {
        CompraEntity compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada."));

        if (!compra.getCartao().getUsuario().getEmail().equals(emailUsuario)) {
            throw new RegraNegocioException("Acesso negado a este comprovante.");
        }

        ComprovanteCompraEntity comprovante = comprovanteCompraRepository.findByCompraId(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprovante não encontrado para esta compra."));

        Path filePath = Paths.get(comprovante.getUrlArquivo()).normalize();
        FileSystemResource resource = new FileSystemResource(filePath);

        if (resource.exists() && resource.isReadable()) {
            return (Resource) resource;
        } else {
            throw new ResourceNotFoundException("Arquivo não encontrado ou inacessível no disco.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CompraResponse> listarCompras(String emailUsuario, Long cartaoId, org.springframework.data.domain.Pageable pageable) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        org.springframework.data.domain.Page<CompraEntity> compras;

        if (cartaoId != null) {
            CartaoEntity cartao = cartaoRepository.findById(cartaoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

            if (!cartao.getUsuario().getId().equals(usuario.getId())) {
                throw new ResourceNotFoundException("Cartão não pertence a este usuário.");
            }
            compras = compraRepository.findByCartaoId(cartaoId, pageable);
        } else {
            compras = compraRepository.findByCartaoUsuarioId(usuario.getId(), pageable);
        }

        return compras.map(compraMapper::toResponse);
    }

}
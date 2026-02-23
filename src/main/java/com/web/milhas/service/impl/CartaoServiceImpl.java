package com.web.milhas.service.impl;

import com.web.milhas.dto.cartao.CartaoRequest;
import com.web.milhas.dto.cartao.CartaoResponse;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.ProgramaPontosEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.RegraNegocioException;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.mapper.CartaoMapper; // Novo import
import com.web.milhas.repository.*;
import com.web.milhas.service.CartaoService;
import com.web.milhas.service.SaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartaoServiceImpl implements CartaoService {

    private final CartaoRepository cartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BandeiraRepository bandeiraRepository;
    private final ProgramaPontosRepository programaPontosRepository;
    private final CompraRepository compraRepository;
    private final SaldoService saldoService;
    private final CartaoMapper cartaoMapper; // Injeção do Mapper

    @Override
    @Transactional
    public CartaoResponse criarCartao(CartaoRequest dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        BandeiraEntity bandeira = bandeiraRepository.findById(dto.bandeiraId())
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira não encontrada."));
        ProgramaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        CartaoEntity cartao = new CartaoEntity();
        cartao.setUsuario(usuario);
        cartao.setNomePersonalizado(dto.nomePersonalizado());
        cartao.setUltimosDigitos(dto.ultimosDigitos());
        cartao.setFatorConversao(dto.fatorConversao());
        cartao.setBandeira(bandeira);
        cartao.setProgramaPontos(programa);
        cartao.setCor(dto.cor());

        CartaoEntity salvo = cartaoRepository.save(cartao);

        saldoService.inicializarSaldoPorCartao(salvo.getUsuario(), salvo.getProgramaPontos());

        return cartaoMapper.toResponse(salvo); // Conversão via Mapper
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartaoResponse> listarCartoes(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        List<CartaoEntity> cartoes = cartaoRepository.findByUsuarioId(usuario.getId());

        return cartaoMapper.toResponseList(cartoes); // Conversão de lista via Mapper
    }

    @Override
    @Transactional
    public void excluirCartao(Long idCartao, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        CartaoEntity cartao = cartaoRepository.findById(idCartao)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Cartão não encontrado para este usuário.");
        }
        cartaoRepository.delete(cartao);
    }

    // Método mapToDTO removido para utilizar o MapStruct

    @Override
    @Transactional
    public CartaoResponse atualizarCartao(Long id, CartaoRequest dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        CartaoEntity cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Cartão não pertence a este usuário.");
        }

        if (compraRepository.existsByCartaoId(id)) {
            throw new RegraNegocioException("Não é possível editar este cartão pois ele já possui compras registradas.");
        }

        BandeiraEntity bandeira = bandeiraRepository.findById(dto.bandeiraId())
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira não encontrada."));
        ProgramaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        cartao.setNomePersonalizado(dto.nomePersonalizado());
        cartao.setUltimosDigitos(dto.ultimosDigitos());
        cartao.setFatorConversao(dto.fatorConversao());
        cartao.setBandeira(bandeira);
        cartao.setProgramaPontos(programa);
        cartao.setCor(dto.cor());

        CartaoEntity atualizado = cartaoRepository.save(cartao);
        return cartaoMapper.toResponse(atualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public CartaoResponse buscarCartaoPorId(Long id, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        CartaoEntity cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cartão não encontrado."));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Cartão não pertence a este usuário.");
        }

        return cartaoMapper.toResponse(cartao);
    }

}
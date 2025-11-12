package com.web.milhas.service.impl;

import com.web.milhas.dto.cartao.CartaoRequest;
import com.web.milhas.dto.cartao.CartaoResponse;
import com.web.milhas.entity.BandeiraEntity;
import com.web.milhas.entity.CartaoEntity;
import com.web.milhas.entity.ProgamaPontosEntity;
import com.web.milhas.entity.UsuarioEntity;
import com.web.milhas.exception.ResourceNotFoundException;
import com.web.milhas.repository.BandeiraRepository;
import com.web.milhas.repository.CartaoRepository;
import com.web.milhas.repository.ProgramaPontosRepository;
import com.web.milhas.repository.UsuarioRepository;
import com.web.milhas.service.CartaoService;
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

    @Override
    @Transactional
    public CartaoResponse criarCartao(CartaoRequest dto, String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        BandeiraEntity bandeira = bandeiraRepository.findById(dto.bandeiraId())
                .orElseThrow(() -> new ResourceNotFoundException("Bandeira não encontrada."));
        ProgamaPontosEntity programa = programaPontosRepository.findById(dto.programaPontosId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa de pontos não encontrado."));

        CartaoEntity cartao = new CartaoEntity();
        cartao.setUsuario(usuario);
        cartao.setNomePersonalizado(dto.nomePersonalizado());
        cartao.setUltimosDigitos(dto.ultimosDigitos());
        cartao.setFatorConversao(dto.fatorConversao());
        cartao.setBandeira(bandeira);
        cartao.setProgramaPontos(programa);

        CartaoEntity salvo = cartaoRepository.save(cartao);
        return mapToDTO(salvo);
    }

    @Override
    public List<CartaoResponse> listarCartoes(String emailUsuario) {
        UsuarioEntity usuario = usuarioRepository.findEntityByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return cartaoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::mapToDTO)
                .toList();
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

    private CartaoResponse mapToDTO(CartaoEntity entity) {
        return new CartaoResponse(
                entity.getId(),
                entity.getNomePersonalizado(),
                entity.getUltimosDigitos(),
                entity.getFatorConversao(),
                entity.getBandeira().getNome(),
                entity.getProgramaPontos().getNome()
        );
    }
}
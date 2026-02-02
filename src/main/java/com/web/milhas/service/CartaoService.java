package com.web.milhas.service;

import com.web.milhas.dto.cartao.CartaoRequest;
import com.web.milhas.dto.cartao.CartaoResponse;
import java.util.List;

public interface CartaoService {
    
    CartaoResponse criarCartao(CartaoRequest dto, String emailUsuario);
    CartaoResponse atualizarCartao(Long id, CartaoRequest dto, String emailUsuario);
    List<CartaoResponse> listarCartoes(String emailUsuario);
    void excluirCartao(Long idCartao, String emailUsuario);
}
package com.web.milhas.controller;

import com.web.milhas.dto.cartao.CartaoRequest;
import com.web.milhas.dto.cartao.CartaoResponse;
import com.web.milhas.service.impl.CartaoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoServiceImpl cartaoServiceImpl;

    @PostMapping
    public ResponseEntity<CartaoResponse> criar(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartaoRequest dto) {

        CartaoResponse response = cartaoServiceImpl.criarCartao(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CartaoResponse>> listar(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(cartaoServiceImpl.listarCartoes(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        cartaoServiceImpl.excluirCartao(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}

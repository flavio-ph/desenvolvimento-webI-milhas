package com.web.milhas.controller;

import com.web.milhas.dto.movimentacao.MovimentacaoPontosResponse;
import com.web.milhas.service.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @GetMapping("/expirando")
    public ResponseEntity<BigDecimal> getPontosExpirando(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "30") int dias) {

        BigDecimal totalExpirando = movimentacaoService.consultarPontosExpirando(userDetails.getUsername(), dias);

        return ResponseEntity.ok(totalExpirando);
    }

    @GetMapping
    public ResponseEntity<Page<MovimentacaoPontosResponse>> listarMovimentacoes(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String programa,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long cartaoId,
            @PageableDefault(size = 10, sort = "dataMovimentacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<MovimentacaoPontosResponse> response = movimentacaoService.listarMovimentacoes(
                userDetails.getUsername(), mes, ano, programa, cartaoId,status, pageable);

        return ResponseEntity.ok(response);
    }


}
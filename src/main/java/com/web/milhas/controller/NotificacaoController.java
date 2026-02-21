package com.web.milhas.controller;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<Page<NotificacaoResponse>> listarMinhas(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificacaoService.listarMinhasNotificacoes(userDetails.getUsername(), pageable));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificacaoService.marcarComoLida(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
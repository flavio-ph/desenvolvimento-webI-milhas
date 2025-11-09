package com.web.milhas.controller;

import com.web.milhas.dto.notificacao.NotificacaoResponse;
import com.web.milhas.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponse>> listarMinhas(
            @AuthenticationPrincipal UserDetails userDetails) {
                return ResponseEntity.ok(notificacaoService.listarMinhasNotificacoes(userDetails.getUsername()));
             }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificacaoService.marcarComoLida(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }



}

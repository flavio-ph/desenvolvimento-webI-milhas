package com.web.milhas.controller;

import com.web.milhas.dto.promocao.PromocaoRequest;
import com.web.milhas.dto.promocao.PromocaoResponse;
import com.web.milhas.service.PromocaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promocoes")
@RequiredArgsConstructor
public class PromocaoController {

    private final PromocaoService promocaoService;

    @GetMapping
    public ResponseEntity<List<PromocaoResponse>> listar() {
        return ResponseEntity.ok(promocaoService.listarAtivas());
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromocaoResponse> salvar(@Valid @RequestBody PromocaoRequest dto) {
        return ResponseEntity.ok(promocaoService.criarPromocao(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        promocaoService.deletarPromocao(id); // Verifique se o nome no service é este
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocaoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PromocaoRequest dto) {
        return ResponseEntity.ok(promocaoService.atualizarPromocao(id, dto));
    }

    @PostMapping("/{id}/participar")
    public ResponseEntity<Void> participar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        promocaoService.participarPromocao(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

}
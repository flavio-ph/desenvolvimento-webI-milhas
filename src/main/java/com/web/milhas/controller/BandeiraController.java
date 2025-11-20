package com.web.milhas.controller;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.service.BandeiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bandeiras")
@RequiredArgsConstructor
public class BandeiraController {

    private final BandeiraService bandeiraService;

    @GetMapping
    public ResponseEntity<List<BandeiraDTO>> listarBandeiras() {
        return ResponseEntity.ok(bandeiraService.listarTodas());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BandeiraDTO> criarBandeira(@Valid @RequestBody BandeiraDTO dto) {
        return ResponseEntity.ok(bandeiraService.salvar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BandeiraDTO> atualizarBandeira(@PathVariable Long id, @Valid @RequestBody BandeiraDTO dto) {
        BandeiraDTO dtoAtualizado = new BandeiraDTO(id, dto.nome());
        return ResponseEntity.ok(bandeiraService.salvar(dtoAtualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarBandeira(@PathVariable Long id) {
        bandeiraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
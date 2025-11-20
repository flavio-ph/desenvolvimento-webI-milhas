package com.web.milhas.controller;

import com.web.milhas.dto.programapontos.ProgramaPontosDTO;
import com.web.milhas.service.ProgramaPontosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programas")
@RequiredArgsConstructor
public class ProgramaPontosController {

    private final ProgramaPontosService programaPontosService;

    @GetMapping
    public ResponseEntity<List<ProgramaPontosDTO>> listarProgramas() {
        return ResponseEntity.ok(programaPontosService.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramaPontosDTO> criarPrograma(@Valid @RequestBody ProgramaPontosDTO dto) {
        return ResponseEntity.ok(programaPontosService.salvar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramaPontosDTO> atualizarPrograma(@PathVariable Long id, @Valid @RequestBody ProgramaPontosDTO dto) {
        ProgramaPontosDTO dtoAtualizado = new ProgramaPontosDTO(id, dto.nome());
        return ResponseEntity.ok(programaPontosService.salvar(dtoAtualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPrograma(@PathVariable Long id) {
        programaPontosService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
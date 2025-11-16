package com.web.milhas.controller;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.dto.programapontos.ProgramaPontosDTO;
import com.web.milhas.service.BandeiraService; // <-- CORREÇÃO: Importa a interface
import com.web.milhas.service.ProgramaPontosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BandeiraService bandeiraService;
    private final ProgramaPontosService programaPontosService;


    @GetMapping("/bandeiras")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BandeiraDTO>> listarBandeiras() {
        return ResponseEntity.ok(bandeiraService.listarTodas());
    }

    @PostMapping("/bandeiras")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BandeiraDTO> criarBandeira(@Valid @RequestBody BandeiraDTO dto) {
        return ResponseEntity.ok(bandeiraService.salvar(dto));
    }

    @PutMapping("/bandeiras/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BandeiraDTO> atualizarBandeira(@PathVariable Long id, @Valid @RequestBody BandeiraDTO dto) {
        BandeiraDTO dtoAtualizado = new BandeiraDTO(id, dto.nome());
        return ResponseEntity.ok(bandeiraService.salvar(dtoAtualizado));
    }

    @DeleteMapping("/bandeiras/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarBandeira(@PathVariable Long id) {
        bandeiraService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/programas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProgramaPontosDTO>> listarProgramas() {
        return ResponseEntity.ok(programaPontosService.listarTodos());
    }

    @PostMapping("/programas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramaPontosDTO> criarPrograma(@Valid @RequestBody ProgramaPontosDTO dto) {
        return ResponseEntity.ok(programaPontosService.salvar(dto));
    }

    @PutMapping("/programas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProgramaPontosDTO> atualizarPrograma(@PathVariable Long id, @Valid @RequestBody ProgramaPontosDTO dto) {
        ProgramaPontosDTO dtoAtualizado = new ProgramaPontosDTO(id, dto.nome());
        return ResponseEntity.ok(programaPontosService.salvar(dtoAtualizado));
    }

    @DeleteMapping("/programas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPrograma(@PathVariable Long id) {
        programaPontosService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
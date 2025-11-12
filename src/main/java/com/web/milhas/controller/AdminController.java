package com.web.milhas.controller;

import com.web.milhas.dto.bandeira.BandeiraDTO;
import com.web.milhas.dto.programapontos.ProgramaPontosDTO;
import com.web.milhas.service.impl.BandeiraServiceImpl;
import com.web.milhas.service.ProgramaPontosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {


        private final BandeiraServiceImpl bandeiraServiceImpl;
        private final ProgramaPontosService programaPontosService;


        @GetMapping("/bandeiras")
        public ResponseEntity<List<BandeiraDTO>> listarBandeiras() {
            return ResponseEntity.ok(bandeiraServiceImpl.listarTodas());
        }

        @PostMapping("/bandeiras")
        public ResponseEntity<BandeiraDTO> criarBandeira(@Valid @RequestBody BandeiraDTO dto) {
            return ResponseEntity.ok(bandeiraServiceImpl.salvar(dto));
        }

        @PutMapping("/bandeiras/{id}")
        public ResponseEntity<BandeiraDTO> atualizarBandeira(@PathVariable Long id, @Valid @RequestBody BandeiraDTO dto) {
            BandeiraDTO dtoAtualizado = new BandeiraDTO(id, dto.nome());
            return ResponseEntity.ok(bandeiraServiceImpl.salvar(dtoAtualizado));
        }

        @DeleteMapping("/bandeiras/{id}")
        public ResponseEntity<Void> deletarBandeira(@PathVariable Long id) {
            bandeiraServiceImpl.deletar(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/programas")
        public ResponseEntity<List<ProgramaPontosDTO>> listarProgramas() {
            return ResponseEntity.ok(programaPontosService.listarTodos());
        }

        @PostMapping("/programas")
        public ResponseEntity<ProgramaPontosDTO> criarPrograma(@Valid @RequestBody ProgramaPontosDTO dto) {
            return ResponseEntity.ok(programaPontosService.salvar(dto));
        }

        @PutMapping("/programas/{id}")
        public ResponseEntity<ProgramaPontosDTO> atualizarPrograma(@PathVariable Long id, @Valid @RequestBody ProgramaPontosDTO dto) {
            ProgramaPontosDTO dtoAtualizado = new ProgramaPontosDTO(id, dto.nome());
            return ResponseEntity.ok(programaPontosService.salvar(dtoAtualizado));
        }

        @DeleteMapping("/programas/{id}")
        public ResponseEntity<Void> deletarPrograma(@PathVariable Long id) {
            programaPontosService.deletar(id);
            return ResponseEntity.noContent().build();
        }
    }


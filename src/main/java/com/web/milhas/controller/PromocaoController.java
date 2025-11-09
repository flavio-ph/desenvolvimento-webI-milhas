package com.web.milhas.controller;

import com.web.milhas.dto.promocao.PromocaoRequest;
import com.web.milhas.dto.promocao.PromocaoResponse;
import com.web.milhas.repository.PromocaoRepository;
import com.web.milhas.service.PromocaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PromocaoResponse> salvar(@Valid @RequestBody PromocaoRequest dto){
        return ResponseEntity.ok(promocaoService.criarPromocao(dto));
    }
}

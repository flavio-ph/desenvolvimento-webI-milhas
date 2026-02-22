package com.web.milhas.controller;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.service.CompraService;
import com.web.milhas.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;
    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<CompraResponse> registrarCompra(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompraRequest dto) {

        CompraResponse response = compraService.registrarCompra(dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{compraId}/upload-comprovante")
    public ResponseEntity<Void> uploadComprovante(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long compraId,
            @RequestParam("arquivo") MultipartFile arquivo) {

        fileUploadService.armazenarComprovante(arquivo, compraId, userDetails.getUsername());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/pendentes/total")
    public ResponseEntity<ResumoPendentesDTO> getResumoPendentes(@AuthenticationPrincipal UserDetails userDetails) {

        ResumoPendentesDTO resumo = compraService.calcularResumoPendentes(userDetails.getUsername());
        return ResponseEntity.ok(resumo);
    }

    @GetMapping
    public ResponseEntity<?> listarCompras(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cartaoId,
            Pageable pageable) {



        return ResponseEntity.ok(compraService.listarCompras(userDetails.getUsername(), cartaoId, pageable));
    }

    @PutMapping("/{id}/creditar")
    public ResponseEntity<Void> creditar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        compraService.creditarCompra(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
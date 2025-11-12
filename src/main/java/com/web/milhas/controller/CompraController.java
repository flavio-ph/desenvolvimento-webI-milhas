package com.web.milhas.controller;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.service.CompraService; // <-- CORREÇÃO: Importa a interface
import com.web.milhas.service.FileUploadService; // <-- CORREÇÃO: Importa a interface
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final CompraService compraService; // <-- CORREÇÃO: Injeta a interface
    private final FileUploadService fileUploadService; // <-- CORREÇÃO: Injeta a interface

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
}
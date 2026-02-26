package com.web.milhas.controller;

import com.web.milhas.dto.compra.CompraRequest;
import com.web.milhas.dto.compra.CompraResponse;
import com.web.milhas.dto.dashboard.ResumoPendentesDTO;
import com.web.milhas.service.CompraService;
import com.web.milhas.service.FileUploadService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaTypeFactory;


import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
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

    @GetMapping("/{compraId}/comprovante")
    public ResponseEntity<Resource> baixarComprovante(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long compraId) {

        Resource file = compraService.baixarComprovante(compraId, userDetails.getUsername());

        MediaType mediaType = MediaTypeFactory.getMediaType((org.springframework.core.io.Resource) file)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ((org.springframework.core.io.Resource) file).getFilename() + "\"")
                .body(file);
    }

}
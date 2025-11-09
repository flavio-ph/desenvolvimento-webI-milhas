package com.web.milhas.controller;

import com.web.milhas.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/movimentacoes/csv")
    public ResponseEntity<byte[]> downloadCsv(
            @AuthenticationPrincipal UserDetails userDetails) {

        byte[] arquivo = relatorioService.gerarCsvMovimentacoes(userDetails.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extrato_movimentacoes.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }

    @GetMapping("/movimentacoes/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @AuthenticationPrincipal UserDetails userDetails) {

        byte[] arquivo = relatorioService.gerarPdfMovimentacoes(userDetails.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=extrato_movimentacoes.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(arquivo);
    }
}
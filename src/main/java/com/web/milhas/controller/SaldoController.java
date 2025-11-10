package com.web.milhas.controller;

import com.web.milhas.dto.saldo.SaldoPontosResponse;
import com.web.milhas.service.SaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/saldos")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoService saldoService;

    @GetMapping("/me")
    public ResponseEntity<List<SaldoPontosResponse>> getMeusSaldos(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        return ResponseEntity.ok(saldoService.consultarSaldos(userDetails.getUsername()));
    }
}
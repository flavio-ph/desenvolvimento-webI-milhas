package com.web.milhas.controller;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;
import com.web.milhas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        DashboardResponseDTO dashboardData = dashboardService.getDashboardData(userDetails.getUsername());
        return ResponseEntity.ok(dashboardData);
    }
}
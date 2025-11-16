package com.web.milhas.service;

import com.web.milhas.dto.dashboard.DashboardResponseDTO;

public interface DashboardService {

    DashboardResponseDTO getDashboardData(String username);
}

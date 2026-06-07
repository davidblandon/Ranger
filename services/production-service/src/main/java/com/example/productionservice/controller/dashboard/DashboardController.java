package com.example.productionservice.controller.dashboard;

import com.example.productionservice.dto.dashboard.DashboardResponse;
import com.example.productionservice.service.dashboard.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getSummary() {
        return dashboardService.getSummary();
    }
}

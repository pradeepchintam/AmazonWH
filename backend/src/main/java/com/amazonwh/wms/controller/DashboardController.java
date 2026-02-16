package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.DashboardKpisDto;
import com.amazonwh.wms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/kpis")
    public DashboardKpisDto getKpis(@RequestParam(required = false) Long dcId) { return service.getKpis(dcId); }

    @GetMapping("/inventory-by-status")
    public List<Map<String, Object>> getInventoryByStatus(@RequestParam(required = false) Long dcId) {
        return service.getInventoryByStatus(dcId);
    }

    @GetMapping("/inbound-trend")
    public List<Map<String, Object>> getInboundTrend(@RequestParam(required = false) Long dcId) {
        return service.getInboundTrend(dcId);
    }

    @GetMapping("/pick-performance")
    public List<Map<String, Object>> getPickPerformance(@RequestParam(required = false) Long dcId) {
        return service.getPickPerformance(dcId);
    }
}

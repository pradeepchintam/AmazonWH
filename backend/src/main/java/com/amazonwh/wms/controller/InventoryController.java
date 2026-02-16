package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @GetMapping("/inventory")
    public Page<InventoryDto> search(
            @RequestParam(required = false) Long dcId,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false) String locationCode,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.search(dcId, skuCode, locationCode, status, PageRequest.of(page, size));
    }

    @PostMapping("/inventory/{id}/adjust")
    public InventoryDto adjust(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return service.adjust(id, ((Number) body.get("qty")).intValue(), (String) body.get("reason"));
    }

    @PostMapping("/inventory/{id}/status")
    public InventoryDto changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changeStatus(id, body.get("newStatus"));
    }

    @PostMapping("/inventory/{id}/transfer")
    public InventoryDto transfer(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return service.transfer(id, ((Number) body.get("toLocationId")).longValue(), ((Number) body.get("qty")).intValue());
    }

    @GetMapping("/cycle-counts")
    public Page<CycleCountDto> getCycleCounts(
            @RequestParam(required = false) Long dcId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllCycleCounts(dcId, PageRequest.of(page, size));
    }

    @PostMapping("/cycle-counts")
    public CycleCountDto createCycleCount(@RequestBody CycleCountDto dto) { return service.createCycleCount(dto); }

    @PostMapping("/cycle-counts/{id}/record")
    public CycleCountDto recordCount(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return service.recordCount(id, ((Number) body.get("countedQty")).intValue(), (String) body.get("notes"));
    }

    @PostMapping("/cycle-counts/{id}/approve")
    public CycleCountDto approveCount(@PathVariable Long id) { return service.approveCount(id); }
}

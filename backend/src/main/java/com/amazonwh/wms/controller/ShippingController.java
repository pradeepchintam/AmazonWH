package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.ShipmentDto;
import com.amazonwh.wms.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService service;

    @GetMapping
    public Page<ShipmentDto> getAll(
            @RequestParam(required = false) Long dcId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAll(dcId, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ShipmentDto getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    public ShipmentDto create(@RequestBody ShipmentDto dto) { return service.create(dto); }

    @PostMapping("/{id}/orders/{orderId}")
    public ShipmentDto addOrder(@PathVariable Long id, @PathVariable Long orderId) {
        return service.addOrder(id, orderId);
    }

    @PostMapping("/{id}/ship")
    public ShipmentDto ship(@PathVariable Long id) { return service.ship(id); }
}

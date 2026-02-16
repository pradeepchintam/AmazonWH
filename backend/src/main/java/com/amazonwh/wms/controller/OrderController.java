package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping("/orders")
    public Page<OrderDto> getAllOrders(
            @RequestParam(required = false) Long dcId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllOrders(dcId, status, PageRequest.of(page, size));
    }
    @GetMapping("/orders/{id}")
    public OrderDto getOrder(@PathVariable Long id) { return service.getOrder(id); }
    @PostMapping("/orders")
    public OrderDto createOrder(@RequestBody OrderDto dto) { return service.createOrder(dto); }
    @PutMapping("/orders/{id}")
    public OrderDto updateOrder(@PathVariable Long id, @RequestBody OrderDto dto) { return service.updateOrder(id, dto); }

    @GetMapping("/waves")
    public Page<WaveDto> getAllWaves(
            @RequestParam(required = false) Long dcId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllWaves(dcId, PageRequest.of(page, size));
    }
    @GetMapping("/waves/{id}")
    public WaveDto getWave(@PathVariable Long id) { return service.getWave(id); }
    @PostMapping("/waves")
    public WaveDto createWave(@RequestBody WaveCreateRequest request) { return service.createWave(request); }
    @PostMapping("/waves/{id}/release")
    public WaveDto releaseWave(@PathVariable Long id) { return service.releaseWave(id); }
}

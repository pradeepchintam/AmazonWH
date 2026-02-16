package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.service.InboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService service;

    @GetMapping("/purchase-orders")
    public Page<PurchaseOrderDto> getAllPOs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllPOs(PageRequest.of(page, size));
    }
    @GetMapping("/purchase-orders/{id}")
    public PurchaseOrderDto getPO(@PathVariable Long id) { return service.getPO(id); }
    @PostMapping("/purchase-orders")
    public PurchaseOrderDto createPO(@RequestBody PurchaseOrderDto dto) { return service.createPO(dto); }
    @PutMapping("/purchase-orders/{id}")
    public PurchaseOrderDto updatePO(@PathVariable Long id, @RequestBody PurchaseOrderDto dto) { return service.updatePO(id, dto); }

    @GetMapping("/asns")
    public Page<AsnDto> getAllASNs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllASNs(PageRequest.of(page, size));
    }
    @GetMapping("/asns/{id}")
    public AsnDto getASN(@PathVariable Long id) { return service.getASN(id); }
    @PostMapping("/asns")
    public AsnDto createASN(@RequestBody AsnDto dto) { return service.createASN(dto); }
    @PostMapping("/asns/{id}/arrive")
    public AsnDto arriveASN(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.arriveASN(id, body.get("doorNumber"));
    }
    @PostMapping("/asns/{id}/receive")
    public AsnDto receiveASN(@PathVariable Long id, @RequestBody List<ReceiveRequest> requests) {
        return service.receiveASN(id, requests);
    }
}

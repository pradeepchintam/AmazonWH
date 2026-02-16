package com.amazonwh.wms.controller;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService service;

    // DCs
    @GetMapping("/dcs")
    public List<DCDto> getAllDCs() { return service.getAllDCs(); }
    @GetMapping("/dcs/{id}")
    public DCDto getDC(@PathVariable Long id) { return service.getDC(id); }
    @PostMapping("/dcs")
    public DCDto createDC(@RequestBody DCDto dto) { return service.createDC(dto); }
    @PutMapping("/dcs/{id}")
    public DCDto updateDC(@PathVariable Long id, @RequestBody DCDto dto) { return service.updateDC(id, dto); }
    @DeleteMapping("/dcs/{id}")
    public ResponseEntity<Void> deleteDC(@PathVariable Long id) { service.deleteDC(id); return ResponseEntity.ok().build(); }

    // SKUs
    @GetMapping("/skus")
    public Page<SkuDto> getAllSkus(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllSkus(PageRequest.of(page, size));
    }
    @GetMapping("/skus/{id}")
    public SkuDto getSku(@PathVariable Long id) { return service.getSku(id); }
    @PostMapping("/skus")
    public SkuDto createSku(@RequestBody SkuDto dto) { return service.createSku(dto); }
    @PutMapping("/skus/{id}")
    public SkuDto updateSku(@PathVariable Long id, @RequestBody SkuDto dto) { return service.updateSku(id, dto); }
    @DeleteMapping("/skus/{id}")
    public ResponseEntity<Void> deleteSku(@PathVariable Long id) { service.deleteSku(id); return ResponseEntity.ok().build(); }

    // Vendors
    @GetMapping("/vendors")
    public Page<VendorDto> getAllVendors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllVendors(PageRequest.of(page, size));
    }
    @GetMapping("/vendors/{id}")
    public VendorDto getVendor(@PathVariable Long id) { return service.getVendor(id); }
    @PostMapping("/vendors")
    public VendorDto createVendor(@RequestBody VendorDto dto) { return service.createVendor(dto); }
    @PutMapping("/vendors/{id}")
    public VendorDto updateVendor(@PathVariable Long id, @RequestBody VendorDto dto) { return service.updateVendor(id, dto); }
    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) { service.deleteVendor(id); return ResponseEntity.ok().build(); }

    // Carriers
    @GetMapping("/carriers")
    public Page<CarrierDto> getAllCarriers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllCarriers(PageRequest.of(page, size));
    }
    @GetMapping("/carriers/{id}")
    public CarrierDto getCarrier(@PathVariable Long id) { return service.getCarrier(id); }
    @PostMapping("/carriers")
    public CarrierDto createCarrier(@RequestBody CarrierDto dto) { return service.createCarrier(dto); }
    @PutMapping("/carriers/{id}")
    public CarrierDto updateCarrier(@PathVariable Long id, @RequestBody CarrierDto dto) { return service.updateCarrier(id, dto); }
    @DeleteMapping("/carriers/{id}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable Long id) { service.deleteCarrier(id); return ResponseEntity.ok().build(); }

    // Locations
    @GetMapping("/locations")
    public Page<LocationDto> getAllLocations(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllLocations(PageRequest.of(page, size));
    }
    @GetMapping("/locations/{id}")
    public LocationDto getLocation(@PathVariable Long id) { return service.getLocation(id); }
    @PostMapping("/locations")
    public LocationDto createLocation(@RequestBody LocationDto dto) { return service.createLocation(dto); }
    @PutMapping("/locations/{id}")
    public LocationDto updateLocation(@PathVariable Long id, @RequestBody LocationDto dto) { return service.updateLocation(id, dto); }
    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) { service.deleteLocation(id); return ResponseEntity.ok().build(); }
}

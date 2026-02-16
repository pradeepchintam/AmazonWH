package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.exception.ResourceNotFoundException;
import com.amazonwh.wms.model.*;
import com.amazonwh.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataService {

    private final SkuRepository skuRepo;
    private final VendorRepository vendorRepo;
    private final CarrierRepository carrierRepo;
    private final LocationRepository locationRepo;
    private final DCRepository dcRepo;

    // DC
    public List<DCDto> getAllDCs() {
        return dcRepo.findAll().stream().map(this::toDCDto).toList();
    }
    public DCDto getDC(Long id) {
        return toDCDto(dcRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("DC not found")));
    }
    @Transactional
    public DCDto createDC(DCDto dto) {
        DC dc = DC.builder().code(dto.getCode()).name(dto.getName()).address(dto.getAddress())
                .city(dto.getCity()).state(dto.getState()).zip(dto.getZip()).build();
        return toDCDto(dcRepo.save(dc));
    }
    @Transactional
    public DCDto updateDC(Long id, DCDto dto) {
        DC dc = dcRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        dc.setName(dto.getName()); dc.setAddress(dto.getAddress()); dc.setCity(dto.getCity());
        dc.setState(dto.getState()); dc.setZip(dto.getZip());
        if (dto.getActive() != null) dc.setActive(dto.getActive());
        return toDCDto(dcRepo.save(dc));
    }
    public void deleteDC(Long id) { dcRepo.deleteById(id); }

    // SKU
    public Page<SkuDto> getAllSkus(Pageable pageable) {
        return skuRepo.findAll(pageable).map(this::toSkuDto);
    }
    public SkuDto getSku(Long id) {
        return toSkuDto(skuRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SKU not found")));
    }
    @Transactional
    public SkuDto createSku(SkuDto dto) {
        Sku sku = Sku.builder().code(dto.getCode()).description(dto.getDescription())
                .uom(dto.getUom() != null ? dto.getUom() : "EACH")
                .weight(dto.getWeight()).length(dto.getLength()).width(dto.getWidth()).height(dto.getHeight()).build();
        return toSkuDto(skuRepo.save(sku));
    }
    @Transactional
    public SkuDto updateSku(Long id, SkuDto dto) {
        Sku sku = skuRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SKU not found"));
        sku.setDescription(dto.getDescription()); sku.setUom(dto.getUom());
        sku.setWeight(dto.getWeight()); sku.setLength(dto.getLength());
        sku.setWidth(dto.getWidth()); sku.setHeight(dto.getHeight());
        if (dto.getActive() != null) sku.setActive(dto.getActive());
        return toSkuDto(skuRepo.save(sku));
    }
    public void deleteSku(Long id) { skuRepo.deleteById(id); }

    // Vendor
    public Page<VendorDto> getAllVendors(Pageable pageable) {
        return vendorRepo.findAll(pageable).map(this::toVendorDto);
    }
    public VendorDto getVendor(Long id) {
        return toVendorDto(vendorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor not found")));
    }
    @Transactional
    public VendorDto createVendor(VendorDto dto) {
        Vendor v = Vendor.builder().code(dto.getCode()).name(dto.getName())
                .contactName(dto.getContactName()).email(dto.getEmail()).phone(dto.getPhone()).address(dto.getAddress()).build();
        return toVendorDto(vendorRepo.save(v));
    }
    @Transactional
    public VendorDto updateVendor(Long id, VendorDto dto) {
        Vendor v = vendorRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        v.setName(dto.getName()); v.setContactName(dto.getContactName()); v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone()); v.setAddress(dto.getAddress());
        if (dto.getActive() != null) v.setActive(dto.getActive());
        return toVendorDto(vendorRepo.save(v));
    }
    public void deleteVendor(Long id) { vendorRepo.deleteById(id); }

    // Carrier
    public Page<CarrierDto> getAllCarriers(Pageable pageable) {
        return carrierRepo.findAll(pageable).map(this::toCarrierDto);
    }
    public CarrierDto getCarrier(Long id) {
        return toCarrierDto(carrierRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found")));
    }
    @Transactional
    public CarrierDto createCarrier(CarrierDto dto) {
        Carrier c = Carrier.builder().code(dto.getCode()).name(dto.getName())
                .scac(dto.getScac()).contactName(dto.getContactName()).phone(dto.getPhone()).build();
        return toCarrierDto(carrierRepo.save(c));
    }
    @Transactional
    public CarrierDto updateCarrier(Long id, CarrierDto dto) {
        Carrier c = carrierRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        c.setName(dto.getName()); c.setScac(dto.getScac()); c.setContactName(dto.getContactName()); c.setPhone(dto.getPhone());
        if (dto.getActive() != null) c.setActive(dto.getActive());
        return toCarrierDto(carrierRepo.save(c));
    }
    public void deleteCarrier(Long id) { carrierRepo.deleteById(id); }

    // Location
    public Page<LocationDto> getAllLocations(Pageable pageable) {
        return locationRepo.findAll(pageable).map(this::toLocationDto);
    }
    public LocationDto getLocation(Long id) {
        return toLocationDto(locationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found")));
    }
    @Transactional
    public LocationDto createLocation(LocationDto dto) {
        DC dc = dcRepo.findById(dto.getDcId() != null ? dto.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        Location l = Location.builder().dc(dc).code(dto.getCode()).zone(dto.getZone())
                .aisle(dto.getAisle()).bay(dto.getBay()).level(dto.getLevel()).position(dto.getPosition())
                .locType(dto.getLocType() != null ? dto.getLocType() : "STORAGE")
                .maxQty(dto.getMaxQty() != null ? dto.getMaxQty() : 9999)
                .pickSequence(dto.getPickSequence() != null ? dto.getPickSequence() : 0).build();
        return toLocationDto(locationRepo.save(l));
    }
    @Transactional
    public LocationDto updateLocation(Long id, LocationDto dto) {
        Location l = locationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        l.setZone(dto.getZone()); l.setAisle(dto.getAisle()); l.setBay(dto.getBay());
        l.setLevel(dto.getLevel()); l.setPosition(dto.getPosition());
        if (dto.getLocType() != null) l.setLocType(dto.getLocType());
        if (dto.getMaxQty() != null) l.setMaxQty(dto.getMaxQty());
        if (dto.getPickSequence() != null) l.setPickSequence(dto.getPickSequence());
        if (dto.getActive() != null) l.setActive(dto.getActive());
        return toLocationDto(locationRepo.save(l));
    }
    public void deleteLocation(Long id) { locationRepo.deleteById(id); }

    // Mappers
    private DCDto toDCDto(DC dc) {
        DCDto d = new DCDto(); d.setId(dc.getId()); d.setCode(dc.getCode()); d.setName(dc.getName());
        d.setAddress(dc.getAddress()); d.setCity(dc.getCity()); d.setState(dc.getState()); d.setZip(dc.getZip()); d.setActive(dc.getActive());
        return d;
    }
    private SkuDto toSkuDto(Sku s) {
        SkuDto d = new SkuDto(); d.setId(s.getId()); d.setCode(s.getCode()); d.setDescription(s.getDescription());
        d.setUom(s.getUom()); d.setWeight(s.getWeight()); d.setLength(s.getLength()); d.setWidth(s.getWidth()); d.setHeight(s.getHeight()); d.setActive(s.getActive());
        return d;
    }
    private VendorDto toVendorDto(Vendor v) {
        VendorDto d = new VendorDto(); d.setId(v.getId()); d.setCode(v.getCode()); d.setName(v.getName());
        d.setContactName(v.getContactName()); d.setEmail(v.getEmail()); d.setPhone(v.getPhone()); d.setAddress(v.getAddress()); d.setActive(v.getActive());
        return d;
    }
    private CarrierDto toCarrierDto(Carrier c) {
        CarrierDto d = new CarrierDto(); d.setId(c.getId()); d.setCode(c.getCode()); d.setName(c.getName());
        d.setScac(c.getScac()); d.setContactName(c.getContactName()); d.setPhone(c.getPhone()); d.setActive(c.getActive());
        return d;
    }
    private LocationDto toLocationDto(Location l) {
        LocationDto d = new LocationDto(); d.setId(l.getId()); d.setDcId(l.getDc().getId()); d.setDcCode(l.getDc().getCode());
        d.setCode(l.getCode()); d.setZone(l.getZone()); d.setAisle(l.getAisle()); d.setBay(l.getBay());
        d.setLevel(l.getLevel()); d.setPosition(l.getPosition()); d.setLocType(l.getLocType());
        d.setMaxQty(l.getMaxQty()); d.setPickSequence(l.getPickSequence()); d.setActive(l.getActive());
        return d;
    }
}

package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.*;
import com.amazonwh.wms.exception.*;
import com.amazonwh.wms.model.*;
import com.amazonwh.wms.model.enums.*;
import com.amazonwh.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipRepo;
    private final ShipmentLineRepository shipLineRepo;
    private final OrderRepository orderRepo;
    private final CarrierRepository carrierRepo;
    private final DCRepository dcRepo;

    public Page<ShipmentDto> getAll(Long dcId, Pageable pageable) {
        return shipRepo.findByDcId(dcId != null ? dcId : 1L, pageable).map(this::toDto);
    }

    public ShipmentDto getById(Long id) {
        return toDto(shipRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shipment not found")));
    }

    @Transactional
    public ShipmentDto create(ShipmentDto dto) {
        DC dc = dcRepo.findById(dto.getDcId() != null ? dto.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        Carrier carrier = dto.getCarrierId() != null ? carrierRepo.findById(dto.getCarrierId()).orElse(null) : null;
        Shipment shipment = Shipment.builder().shipmentNumber(dto.getShipmentNumber()).dc(dc).carrier(carrier)
                .trailerNumber(dto.getTrailerNumber()).doorNumber(dto.getDoorNumber()).notes(dto.getNotes()).build();
        return toDto(shipRepo.save(shipment));
    }

    @Transactional
    public ShipmentDto addOrder(Long shipmentId, Long orderId) {
        Shipment shipment = shipRepo.findById(shipmentId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        WmsOrder order = orderRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.PICKED) throw new BadRequestException("Order must be in PICKED status");
        if (shipment.getStatus() != ShipmentStatus.CREATED && shipment.getStatus() != ShipmentStatus.LOADING) {
            throw new BadRequestException("Shipment not in loadable status");
        }
        ShipmentLine line = ShipmentLine.builder().shipment(shipment).order(order).build();
        shipLineRepo.save(line);
        shipment.setStatus(ShipmentStatus.LOADING);
        return toDto(shipRepo.save(shipment));
    }

    @Transactional
    public ShipmentDto ship(Long shipmentId) {
        Shipment shipment = shipRepo.findById(shipmentId).orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
        if (shipment.getLines().isEmpty()) throw new BadRequestException("No orders loaded");
        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipment.setShippedAt(LocalDateTime.now());
        shipment.setBolNumber("BOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        // Update order statuses
        for (ShipmentLine line : shipment.getLines()) {
            WmsOrder order = line.getOrder();
            order.setStatus(OrderStatus.SHIPPED);
            orderRepo.save(order);
            line.setStatus("SHIPPED");
            shipLineRepo.save(line);
        }
        return toDto(shipRepo.save(shipment));
    }

    private ShipmentDto toDto(Shipment s) {
        ShipmentDto d = new ShipmentDto();
        d.setId(s.getId()); d.setShipmentNumber(s.getShipmentNumber()); d.setDcId(s.getDc().getId());
        if (s.getCarrier() != null) { d.setCarrierId(s.getCarrier().getId()); d.setCarrierName(s.getCarrier().getName()); }
        d.setStatus(s.getStatus().name()); d.setBolNumber(s.getBolNumber());
        d.setTrailerNumber(s.getTrailerNumber()); d.setDoorNumber(s.getDoorNumber());
        d.setShippedAt(s.getShippedAt() != null ? s.getShippedAt().toString() : null); d.setNotes(s.getNotes());
        d.setLines(s.getLines().stream().map(this::toLineDto).toList());
        return d;
    }

    private ShipmentLineDto toLineDto(ShipmentLine l) {
        ShipmentLineDto d = new ShipmentLineDto();
        d.setId(l.getId()); d.setOrderId(l.getOrder().getId());
        d.setOrderNumber(l.getOrder().getOrderNumber()); d.setStatus(l.getStatus());
        return d;
    }
}

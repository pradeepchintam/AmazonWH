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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderLineRepository orderLineRepo;
    private final WaveRepository waveRepo;
    private final SkuRepository skuRepo;
    private final CarrierRepository carrierRepo;
    private final DCRepository dcRepo;
    private final InventoryRepository invRepo;
    private final PickTaskRepository pickTaskRepo;

    // Orders
    public Page<OrderDto> getAllOrders(Long dcId, String status, Pageable pageable) {
        Long dc = dcId != null ? dcId : 1L;
        if (status != null && !status.isEmpty()) {
            return orderRepo.findByDcIdAndStatus(dc, OrderStatus.valueOf(status), pageable).map(this::toOrderDto);
        }
        return orderRepo.findByDcId(dc, pageable).map(this::toOrderDto);
    }

    public OrderDto getOrder(Long id) {
        return toOrderDto(orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Transactional
    public OrderDto createOrder(OrderDto dto) {
        DC dc = dcRepo.findById(dto.getDcId() != null ? dto.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        Carrier carrier = dto.getCarrierId() != null ? carrierRepo.findById(dto.getCarrierId()).orElse(null) : null;
        WmsOrder order = WmsOrder.builder().orderNumber(dto.getOrderNumber()).dc(dc)
                .priority(dto.getPriority() != null ? dto.getPriority() : 5)
                .customerName(dto.getCustomerName()).shipToAddress(dto.getShipToAddress())
                .shipToCity(dto.getShipToCity()).shipToState(dto.getShipToState()).shipToZip(dto.getShipToZip())
                .carrier(carrier).requiredDate(dto.getRequiredDate() != null ? LocalDate.parse(dto.getRequiredDate()) : null)
                .notes(dto.getNotes()).build();
        if (dto.getLines() != null) {
            for (int i = 0; i < dto.getLines().size(); i++) {
                OrderLineDto ld = dto.getLines().get(i);
                Sku sku = skuRepo.findById(ld.getSkuId()).orElseThrow(() -> new ResourceNotFoundException("SKU not found"));
                OrderLine line = OrderLine.builder().order(order).sku(sku)
                        .orderedQty(ld.getOrderedQty()).lineNumber(ld.getLineNumber() != null ? ld.getLineNumber() : i + 1).build();
                order.getLines().add(line);
            }
        }
        return toOrderDto(orderRepo.save(order));
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderDto dto) {
        WmsOrder order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (dto.getCustomerName() != null) order.setCustomerName(dto.getCustomerName());
        if (dto.getPriority() != null) order.setPriority(dto.getPriority());
        if (dto.getNotes() != null) order.setNotes(dto.getNotes());
        return toOrderDto(orderRepo.save(order));
    }

    // Waves
    public Page<WaveDto> getAllWaves(Long dcId, Pageable pageable) {
        return waveRepo.findByDcId(dcId != null ? dcId : 1L, pageable).map(this::toWaveDto);
    }

    public WaveDto getWave(Long id) {
        return toWaveDto(waveRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Wave not found")));
    }

    @Transactional
    public WaveDto createWave(WaveCreateRequest req) {
        DC dc = dcRepo.findById(req.getDcId() != null ? req.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        String waveNum = "W" + System.currentTimeMillis();
        Wave wave = Wave.builder().waveNumber(waveNum).dc(dc).build();
        wave = waveRepo.save(wave);
        for (Long orderId : req.getOrderIds()) {
            WmsOrder order = orderRepo.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
            if (order.getWave() != null) throw new BadRequestException("Order " + order.getOrderNumber() + " already in a wave");
            order.setWave(wave);
            orderRepo.save(order);
        }
        return toWaveDto(waveRepo.findById(wave.getId()).orElseThrow());
    }

    @Transactional
    public WaveDto releaseWave(Long waveId) {
        Wave wave = waveRepo.findById(waveId).orElseThrow(() -> new ResourceNotFoundException("Wave not found"));
        if (wave.getStatus() != WaveStatus.CREATED) throw new BadRequestException("Wave already released");

        List<WmsOrder> orders = wave.getOrders();
        for (WmsOrder order : orders) {
            for (OrderLine line : order.getLines()) {
                int needed = line.getOrderedQty();
                List<Inventory> available = invRepo.findBySkuIdAndDcIdAndStatus(line.getSku().getId(), wave.getDc().getId(), InventoryStatus.AVAILABLE);
                int totalAllocated = 0;
                for (Inventory inv : available) {
                    if (totalAllocated >= needed) break;
                    int canAllocate = Math.min(needed - totalAllocated, inv.getQtyOnHand() - inv.getQtyAllocated());
                    if (canAllocate <= 0) continue;
                    inv.setQtyAllocated(inv.getQtyAllocated() + canAllocate);
                    invRepo.save(inv);
                    // Create pick task
                    PickTask pt = PickTask.builder().wave(wave).order(order).orderLine(line)
                            .sku(line.getSku()).fromLocation(inv.getLocation()).dc(wave.getDc())
                            .qtyToPick(canAllocate).pickSequence(inv.getLocation().getPickSequence()).build();
                    pickTaskRepo.save(pt);
                    totalAllocated += canAllocate;
                }
                line.setAllocatedQty(totalAllocated);
                line.setStatus(totalAllocated >= needed ? OrderLineStatus.ALLOCATED : OrderLineStatus.SHORT);
                orderLineRepo.save(line);
            }
            order.setStatus(OrderStatus.ALLOCATED);
            orderRepo.save(order);
        }
        wave.setStatus(WaveStatus.RELEASED);
        wave.setReleasedAt(LocalDateTime.now());
        return toWaveDto(waveRepo.save(wave));
    }

    // Mappers
    private OrderDto toOrderDto(WmsOrder o) {
        OrderDto d = new OrderDto();
        d.setId(o.getId()); d.setOrderNumber(o.getOrderNumber()); d.setDcId(o.getDc().getId());
        d.setStatus(o.getStatus().name()); d.setPriority(o.getPriority());
        d.setCustomerName(o.getCustomerName()); d.setShipToAddress(o.getShipToAddress());
        d.setShipToCity(o.getShipToCity()); d.setShipToState(o.getShipToState()); d.setShipToZip(o.getShipToZip());
        if (o.getCarrier() != null) { d.setCarrierId(o.getCarrier().getId()); d.setCarrierName(o.getCarrier().getName()); }
        if (o.getWave() != null) { d.setWaveId(o.getWave().getId()); d.setWaveNumber(o.getWave().getWaveNumber()); }
        d.setRequiredDate(o.getRequiredDate() != null ? o.getRequiredDate().toString() : null);
        d.setNotes(o.getNotes());
        d.setLines(o.getLines().stream().map(this::toOrderLineDto).toList());
        return d;
    }

    private OrderLineDto toOrderLineDto(OrderLine l) {
        OrderLineDto d = new OrderLineDto();
        d.setId(l.getId()); d.setSkuId(l.getSku().getId()); d.setSkuCode(l.getSku().getCode());
        d.setLineNumber(l.getLineNumber()); d.setOrderedQty(l.getOrderedQty());
        d.setAllocatedQty(l.getAllocatedQty()); d.setPickedQty(l.getPickedQty()); d.setStatus(l.getStatus().name());
        return d;
    }

    private WaveDto toWaveDto(Wave w) {
        WaveDto d = new WaveDto();
        d.setId(w.getId()); d.setWaveNumber(w.getWaveNumber()); d.setDcId(w.getDc().getId());
        d.setStatus(w.getStatus().name());
        d.setReleasedAt(w.getReleasedAt() != null ? w.getReleasedAt().toString() : null);
        d.setCompletedAt(w.getCompletedAt() != null ? w.getCompletedAt().toString() : null);
        d.setOrderCount(w.getOrders() != null ? w.getOrders().size() : 0);
        d.setOrderIds(w.getOrders() != null ? w.getOrders().stream().map(WmsOrder::getId).toList() : List.of());
        return d;
    }
}

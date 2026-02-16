package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.PickTaskDto;
import com.amazonwh.wms.exception.*;
import com.amazonwh.wms.model.*;
import com.amazonwh.wms.model.enums.*;
import com.amazonwh.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PickingService {

    private final PickTaskRepository pickRepo;
    private final AppUserRepository userRepo;
    private final InventoryRepository invRepo;
    private final OrderLineRepository orderLineRepo;
    private final OrderRepository orderRepo;
    private final WaveRepository waveRepo;

    public Page<PickTaskDto> getAll(Long dcId, Pageable pageable) {
        return pickRepo.findByDcId(dcId != null ? dcId : 1L, pageable).map(this::toDto);
    }

    public List<PickTaskDto> getMyTasks(Long userId) {
        return pickRepo.findByAssignedToIdAndStatusIn(userId,
                List.of(PickTaskStatus.ASSIGNED, PickTaskStatus.IN_PROGRESS)).stream().map(this::toDto).toList();
    }

    @Transactional
    public PickTaskDto assign(Long taskId, Long userId) {
        PickTask task = pickRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Pick task not found"));
        AppUser user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (task.getStatus() != PickTaskStatus.PENDING) throw new BadRequestException("Task not in PENDING status");
        task.setAssignedTo(user);
        task.setStatus(PickTaskStatus.ASSIGNED);
        return toDto(pickRepo.save(task));
    }

    @Transactional
    public PickTaskDto start(Long taskId) {
        PickTask task = pickRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Pick task not found"));
        if (task.getStatus() != PickTaskStatus.ASSIGNED) throw new BadRequestException("Task not assigned");
        task.setStatus(PickTaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        // Update order status
        WmsOrder order = task.getOrder();
        if (order.getStatus() == OrderStatus.ALLOCATED) {
            order.setStatus(OrderStatus.PICKING);
            orderRepo.save(order);
        }
        return toDto(pickRepo.save(task));
    }

    @Transactional
    public PickTaskDto complete(Long taskId, Integer qtyPicked) {
        PickTask task = pickRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Pick task not found"));
        if (task.getStatus() != PickTaskStatus.IN_PROGRESS) throw new BadRequestException("Task not in progress");
        task.setQtyPicked(qtyPicked);
        task.setStatus(PickTaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        pickRepo.save(task);

        // Update inventory - decrement on hand, decrement allocated
        List<Inventory> invs = invRepo.findBySkuIdAndDcIdAndStatus(task.getSku().getId(), task.getDc().getId(), InventoryStatus.AVAILABLE);
        int toDecrement = qtyPicked;
        for (Inventory inv : invs) {
            if (toDecrement <= 0) break;
            if (inv.getLocation().getId().equals(task.getFromLocation().getId())) {
                int dec = Math.min(toDecrement, inv.getQtyOnHand());
                inv.setQtyOnHand(inv.getQtyOnHand() - dec);
                inv.setQtyAllocated(Math.max(0, inv.getQtyAllocated() - dec));
                invRepo.save(inv);
                toDecrement -= dec;
            }
        }

        // Update order line picked qty
        OrderLine ol = task.getOrderLine();
        ol.setPickedQty(ol.getPickedQty() + qtyPicked);
        if (ol.getPickedQty() >= ol.getOrderedQty()) ol.setStatus(OrderLineStatus.PICKED);
        else ol.setStatus(OrderLineStatus.PICKING);
        orderLineRepo.save(ol);

        // Check if all lines for order are picked
        checkOrderComplete(task.getOrder());
        checkWaveComplete(task.getWave());

        return toDto(task);
    }

    @Transactional
    public PickTaskDto shortPick(Long taskId, Integer qtyPicked) {
        PickTask task = pickRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Pick task not found"));
        if (task.getStatus() != PickTaskStatus.IN_PROGRESS) throw new BadRequestException("Task not in progress");
        task.setQtyPicked(qtyPicked);
        task.setStatus(PickTaskStatus.SHORT);
        task.setCompletedAt(LocalDateTime.now());
        pickRepo.save(task);

        OrderLine ol = task.getOrderLine();
        ol.setPickedQty(ol.getPickedQty() + qtyPicked);
        ol.setStatus(OrderLineStatus.SHORT);
        orderLineRepo.save(ol);

        checkOrderComplete(task.getOrder());
        checkWaveComplete(task.getWave());

        return toDto(task);
    }

    private void checkOrderComplete(WmsOrder order) {
        List<OrderLine> lines = orderLineRepo.findByOrderId(order.getId());
        boolean allPicked = lines.stream().allMatch(l -> l.getStatus() == OrderLineStatus.PICKED || l.getStatus() == OrderLineStatus.SHORT);
        if (allPicked) {
            order.setStatus(OrderStatus.PICKED);
            orderRepo.save(order);
        }
    }

    private void checkWaveComplete(Wave wave) {
        List<PickTask> tasks = pickRepo.findByWaveId(wave.getId());
        boolean allDone = tasks.stream().allMatch(t -> t.getStatus() == PickTaskStatus.COMPLETED || t.getStatus() == PickTaskStatus.SHORT);
        if (allDone) {
            wave.setStatus(WaveStatus.COMPLETED);
            wave.setCompletedAt(LocalDateTime.now());
            waveRepo.save(wave);
        }
    }

    private PickTaskDto toDto(PickTask t) {
        PickTaskDto d = new PickTaskDto();
        d.setId(t.getId()); d.setWaveId(t.getWave().getId()); d.setWaveNumber(t.getWave().getWaveNumber());
        d.setOrderId(t.getOrder().getId()); d.setOrderNumber(t.getOrder().getOrderNumber());
        d.setOrderLineId(t.getOrderLine().getId()); d.setSkuId(t.getSku().getId());
        d.setSkuCode(t.getSku().getCode()); d.setSkuDescription(t.getSku().getDescription());
        d.setFromLocationId(t.getFromLocation().getId()); d.setFromLocationCode(t.getFromLocation().getCode());
        d.setDcId(t.getDc().getId()); d.setQtyToPick(t.getQtyToPick()); d.setQtyPicked(t.getQtyPicked());
        d.setStatus(t.getStatus().name()); d.setPickSequence(t.getPickSequence());
        if (t.getAssignedTo() != null) { d.setAssignedTo(t.getAssignedTo().getId()); d.setAssignedToName(t.getAssignedTo().getFullName()); }
        return d;
    }
}

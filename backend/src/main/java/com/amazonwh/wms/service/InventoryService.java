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
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository invRepo;
    private final CycleCountRepository ccRepo;
    private final LocationRepository locationRepo;
    private final DCRepository dcRepo;

    public Page<InventoryDto> search(Long dcId, String skuCode, String locationCode, String status, Pageable pageable) {
        InventoryStatus invStatus = status != null && !status.isEmpty() ? InventoryStatus.valueOf(status) : null;
        Long dc = dcId != null ? dcId : 1L;
        return invRepo.search(dc, skuCode, locationCode, invStatus, pageable).map(this::toDto);
    }

    @Transactional
    public InventoryDto adjust(Long id, Integer qty, String reason) {
        Inventory inv = invRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        inv.setQtyOnHand(inv.getQtyOnHand() + qty);
        if (inv.getQtyOnHand() < 0) throw new BadRequestException("Cannot adjust below zero");
        return toDto(invRepo.save(inv));
    }

    @Transactional
    public InventoryDto changeStatus(Long id, String newStatus) {
        Inventory inv = invRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        inv.setStatus(InventoryStatus.valueOf(newStatus));
        return toDto(invRepo.save(inv));
    }

    @Transactional
    public InventoryDto transfer(Long id, Long toLocationId, Integer qty) {
        Inventory from = invRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        if (qty > from.getQtyOnHand() - from.getQtyAllocated()) throw new BadRequestException("Insufficient available qty");
        Location toLoc = locationRepo.findById(toLocationId).orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        from.setQtyOnHand(from.getQtyOnHand() - qty);
        invRepo.save(from);

        Inventory to = invRepo.findBySkuIdAndLocationIdAndStatusAndLotNumber(
                from.getSku().getId(), toLoc.getId(), from.getStatus(), from.getLotNumber())
                .orElse(Inventory.builder().sku(from.getSku()).location(toLoc).dc(from.getDc())
                        .status(from.getStatus()).lotNumber(from.getLotNumber()).build());
        to.setQtyOnHand(to.getQtyOnHand() + qty);
        return toDto(invRepo.save(to));
    }

    // Cycle Counts
    public Page<CycleCountDto> getAllCycleCounts(Long dcId, Pageable pageable) {
        return ccRepo.findByDcId(dcId != null ? dcId : 1L, pageable).map(this::toCcDto);
    }

    @Transactional
    public CycleCountDto createCycleCount(CycleCountDto dto) {
        DC dc = dcRepo.findById(dto.getDcId() != null ? dto.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        Location loc = locationRepo.findById(dto.getLocationId()).orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        List<Inventory> invAtLoc = invRepo.findByLocationId(loc.getId());
        int systemQty = invAtLoc.stream().mapToInt(Inventory::getQtyOnHand).sum();
        CycleCount cc = CycleCount.builder().dc(dc).location(loc).systemQty(systemQty).build();
        return toCcDto(ccRepo.save(cc));
    }

    @Transactional
    public CycleCountDto recordCount(Long id, Integer countedQty, String notes) {
        CycleCount cc = ccRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cycle count not found"));
        cc.setCountedQty(countedQty);
        cc.setVariance(countedQty - (cc.getSystemQty() != null ? cc.getSystemQty() : 0));
        cc.setStatus(CycleCountStatus.COMPLETED);
        cc.setCountedAt(LocalDateTime.now());
        if (notes != null) cc.setNotes(notes);
        return toCcDto(ccRepo.save(cc));
    }

    @Transactional
    public CycleCountDto approveCount(Long id) {
        CycleCount cc = ccRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cycle count not found"));
        if (cc.getStatus() != CycleCountStatus.COMPLETED) throw new BadRequestException("Can only approve completed counts");
        cc.setStatus(CycleCountStatus.APPROVED);
        // Adjust inventory if variance exists
        if (cc.getVariance() != null && cc.getVariance() != 0) {
            List<Inventory> invs = invRepo.findByLocationId(cc.getLocation().getId());
            if (!invs.isEmpty()) {
                Inventory inv = invs.get(0);
                inv.setQtyOnHand(inv.getQtyOnHand() + cc.getVariance());
                invRepo.save(inv);
            }
        }
        return toCcDto(ccRepo.save(cc));
    }

    private InventoryDto toDto(Inventory i) {
        InventoryDto d = new InventoryDto();
        d.setId(i.getId()); d.setSkuId(i.getSku().getId()); d.setSkuCode(i.getSku().getCode());
        d.setSkuDescription(i.getSku().getDescription()); d.setLocationId(i.getLocation().getId());
        d.setLocationCode(i.getLocation().getCode()); d.setDcId(i.getDc().getId());
        d.setStatus(i.getStatus().name()); d.setLotNumber(i.getLotNumber());
        d.setQtyOnHand(i.getQtyOnHand()); d.setQtyAllocated(i.getQtyAllocated());
        return d;
    }

    private CycleCountDto toCcDto(CycleCount cc) {
        CycleCountDto d = new CycleCountDto();
        d.setId(cc.getId()); d.setDcId(cc.getDc().getId()); d.setLocationId(cc.getLocation().getId());
        d.setLocationCode(cc.getLocation().getCode());
        if (cc.getSku() != null) { d.setSkuId(cc.getSku().getId()); d.setSkuCode(cc.getSku().getCode()); }
        d.setStatus(cc.getStatus().name()); d.setSystemQty(cc.getSystemQty());
        d.setCountedQty(cc.getCountedQty()); d.setVariance(cc.getVariance());
        d.setCountedAt(cc.getCountedAt() != null ? cc.getCountedAt().toString() : null); d.setNotes(cc.getNotes());
        return d;
    }
}

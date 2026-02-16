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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboundService {

    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderLineRepository poLineRepo;
    private final AsnRepository asnRepo;
    private final AsnLineRepository asnLineRepo;
    private final InventoryRepository invRepo;
    private final SkuRepository skuRepo;
    private final VendorRepository vendorRepo;
    private final CarrierRepository carrierRepo;
    private final LocationRepository locationRepo;
    private final DCRepository dcRepo;

    // PO
    public Page<PurchaseOrderDto> getAllPOs(Pageable pageable) {
        return poRepo.findAll(pageable).map(this::toPoDto);
    }

    public PurchaseOrderDto getPO(Long id) {
        return toPoDto(poRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("PO not found")));
    }

    @Transactional
    public PurchaseOrderDto createPO(PurchaseOrderDto dto) {
        Vendor vendor = vendorRepo.findById(dto.getVendorId()).orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        DC dc = dcRepo.findById(dto.getDcId() != null ? dto.getDcId() : 1L).orElseThrow(() -> new ResourceNotFoundException("DC not found"));
        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(dto.getPoNumber()).vendor(vendor).dc(dc)
                .expectedDate(dto.getExpectedDate() != null ? LocalDate.parse(dto.getExpectedDate()) : null)
                .notes(dto.getNotes()).build();
        if (dto.getLines() != null) {
            for (int i = 0; i < dto.getLines().size(); i++) {
                PurchaseOrderLineDto ld = dto.getLines().get(i);
                Sku sku = skuRepo.findById(ld.getSkuId()).orElseThrow(() -> new ResourceNotFoundException("SKU not found"));
                PurchaseOrderLine line = PurchaseOrderLine.builder()
                        .purchaseOrder(po).sku(sku).expectedQty(ld.getExpectedQty())
                        .lineNumber(ld.getLineNumber() != null ? ld.getLineNumber() : i + 1).build();
                po.getLines().add(line);
            }
        }
        return toPoDto(poRepo.save(po));
    }

    @Transactional
    public PurchaseOrderDto updatePO(Long id, PurchaseOrderDto dto) {
        PurchaseOrder po = poRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("PO not found"));
        if (dto.getExpectedDate() != null) po.setExpectedDate(LocalDate.parse(dto.getExpectedDate()));
        if (dto.getNotes() != null) po.setNotes(dto.getNotes());
        return toPoDto(poRepo.save(po));
    }

    // ASN
    public Page<AsnDto> getAllASNs(Pageable pageable) {
        return asnRepo.findAll(pageable).map(this::toAsnDto);
    }

    public AsnDto getASN(Long id) {
        return toAsnDto(asnRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("ASN not found")));
    }

    @Transactional
    public AsnDto createASN(AsnDto dto) {
        PurchaseOrder po = poRepo.findById(dto.getPoId()).orElseThrow(() -> new ResourceNotFoundException("PO not found"));
        Carrier carrier = dto.getCarrierId() != null ? carrierRepo.findById(dto.getCarrierId()).orElse(null) : null;
        Asn asn = Asn.builder().asnNumber(dto.getAsnNumber()).purchaseOrder(po).carrier(carrier)
                .expectedArrival(dto.getExpectedArrival() != null ? LocalDate.parse(dto.getExpectedArrival()) : null)
                .trailerNumber(dto.getTrailerNumber()).notes(dto.getNotes()).build();
        // Auto-create ASN lines from PO lines
        for (PurchaseOrderLine poLine : po.getLines()) {
            int remaining = poLine.getExpectedQty() - poLine.getReceivedQty();
            if (remaining > 0) {
                AsnLine asnLine = AsnLine.builder().asn(asn).poLine(poLine).sku(poLine.getSku())
                        .expectedQty(remaining).build();
                asn.getLines().add(asnLine);
            }
        }
        return toAsnDto(asnRepo.save(asn));
    }

    @Transactional
    public AsnDto arriveASN(Long id, String doorNumber) {
        Asn asn = asnRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("ASN not found"));
        if (asn.getStatus() != AsnStatus.CREATED && asn.getStatus() != AsnStatus.IN_TRANSIT) {
            throw new BadRequestException("ASN cannot be arrived in status: " + asn.getStatus());
        }
        asn.setStatus(AsnStatus.ARRIVED);
        asn.setArrivedAt(LocalDateTime.now());
        asn.setDoorNumber(doorNumber);
        return toAsnDto(asnRepo.save(asn));
    }

    @Transactional
    public AsnDto receiveASN(Long asnId, List<ReceiveRequest> requests) {
        Asn asn = asnRepo.findById(asnId).orElseThrow(() -> new ResourceNotFoundException("ASN not found"));
        if (asn.getStatus() != AsnStatus.ARRIVED && asn.getStatus() != AsnStatus.RECEIVING) {
            throw new BadRequestException("ASN cannot be received in status: " + asn.getStatus());
        }
        asn.setStatus(AsnStatus.RECEIVING);
        PurchaseOrder po = asn.getPurchaseOrder();
        boolean allReceived = true;

        for (ReceiveRequest req : requests) {
            if (req.getQty() == null || req.getQty() <= 0) continue;
            AsnLine asnLine = asnLineRepo.findById(req.getAsnLineId())
                    .orElseThrow(() -> new ResourceNotFoundException("ASN Line not found"));
            Location location = locationRepo.findById(req.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

            asnLine.setReceivedQty(asnLine.getReceivedQty() + req.getQty());
            if (asnLine.getReceivedQty() >= asnLine.getExpectedQty()) {
                asnLine.setStatus(AsnLineStatus.RECEIVED);
            } else {
                asnLine.setStatus(AsnLineStatus.PARTIAL);
                allReceived = false;
            }
            asnLineRepo.save(asnLine);

            // Update PO line
            PurchaseOrderLine poLine = asnLine.getPoLine();
            poLine.setReceivedQty(poLine.getReceivedQty() + req.getQty());
            if (poLine.getReceivedQty() >= poLine.getExpectedQty()) {
                poLine.setStatus(PoLineStatus.RECEIVED);
            } else {
                poLine.setStatus(PoLineStatus.PARTIAL);
            }
            poLineRepo.save(poLine);

            // Create/update inventory
            String lotNumber = req.getLotNumber() != null ? req.getLotNumber() : asnLine.getLotNumber();
            Inventory inv = invRepo.findBySkuIdAndLocationIdAndStatusAndLotNumber(
                    asnLine.getSku().getId(), location.getId(), InventoryStatus.AVAILABLE, lotNumber)
                    .orElse(Inventory.builder().sku(asnLine.getSku()).location(location)
                            .dc(po.getDc()).status(InventoryStatus.AVAILABLE).lotNumber(lotNumber).build());
            inv.setQtyOnHand(inv.getQtyOnHand() + req.getQty());
            invRepo.save(inv);
        }

        if (allReceived && asn.getLines().stream().allMatch(l -> l.getStatus() == AsnLineStatus.RECEIVED)) {
            asn.setStatus(AsnStatus.RECEIVED);
        }
        // Update PO status
        boolean allPoLinesReceived = po.getLines().stream().allMatch(l -> l.getStatus() == PoLineStatus.RECEIVED);
        boolean anyReceived = po.getLines().stream().anyMatch(l -> l.getReceivedQty() > 0);
        if (allPoLinesReceived) {
            po.setStatus(PoStatus.RECEIVED);
        } else if (anyReceived) {
            po.setStatus(PoStatus.PARTIAL);
        }
        poRepo.save(po);

        return toAsnDto(asnRepo.save(asn));
    }

    // Mappers
    private PurchaseOrderDto toPoDto(PurchaseOrder po) {
        PurchaseOrderDto d = new PurchaseOrderDto();
        d.setId(po.getId()); d.setPoNumber(po.getPoNumber()); d.setVendorId(po.getVendor().getId());
        d.setVendorName(po.getVendor().getName()); d.setDcId(po.getDc().getId());
        d.setStatus(po.getStatus().name());
        d.setExpectedDate(po.getExpectedDate() != null ? po.getExpectedDate().toString() : null);
        d.setNotes(po.getNotes());
        d.setLines(po.getLines().stream().map(this::toPoLineDto).toList());
        return d;
    }

    private PurchaseOrderLineDto toPoLineDto(PurchaseOrderLine l) {
        PurchaseOrderLineDto d = new PurchaseOrderLineDto();
        d.setId(l.getId()); d.setSkuId(l.getSku().getId()); d.setSkuCode(l.getSku().getCode());
        d.setExpectedQty(l.getExpectedQty()); d.setReceivedQty(l.getReceivedQty());
        d.setLineNumber(l.getLineNumber()); d.setStatus(l.getStatus().name());
        return d;
    }

    private AsnDto toAsnDto(Asn asn) {
        AsnDto d = new AsnDto();
        d.setId(asn.getId()); d.setAsnNumber(asn.getAsnNumber());
        d.setPoId(asn.getPurchaseOrder().getId()); d.setPoNumber(asn.getPurchaseOrder().getPoNumber());
        if (asn.getCarrier() != null) { d.setCarrierId(asn.getCarrier().getId()); d.setCarrierName(asn.getCarrier().getName()); }
        d.setStatus(asn.getStatus().name());
        d.setExpectedArrival(asn.getExpectedArrival() != null ? asn.getExpectedArrival().toString() : null);
        d.setArrivedAt(asn.getArrivedAt() != null ? asn.getArrivedAt().toString() : null);
        d.setTrailerNumber(asn.getTrailerNumber()); d.setDoorNumber(asn.getDoorNumber()); d.setNotes(asn.getNotes());
        d.setLines(asn.getLines().stream().map(this::toAsnLineDto).toList());
        return d;
    }

    private AsnLineDto toAsnLineDto(AsnLine l) {
        AsnLineDto d = new AsnLineDto();
        d.setId(l.getId()); d.setPoLineId(l.getPoLine().getId()); d.setSkuId(l.getSku().getId());
        d.setSkuCode(l.getSku().getCode()); d.setExpectedQty(l.getExpectedQty());
        d.setReceivedQty(l.getReceivedQty()); d.setLotNumber(l.getLotNumber()); d.setStatus(l.getStatus().name());
        return d;
    }
}

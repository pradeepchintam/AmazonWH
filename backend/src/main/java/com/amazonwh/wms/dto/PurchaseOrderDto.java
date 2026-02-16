package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Long id;
    private String poNumber;
    private Long vendorId;
    private String vendorName;
    private Long dcId;
    private String status;
    private String expectedDate;
    private String notes;
    private List<PurchaseOrderLineDto> lines;
}

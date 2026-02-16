package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class InventoryDto {
    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuDescription;
    private Long locationId;
    private String locationCode;
    private Long dcId;
    private String status;
    private String lotNumber;
    private Integer qtyOnHand;
    private Integer qtyAllocated;
}

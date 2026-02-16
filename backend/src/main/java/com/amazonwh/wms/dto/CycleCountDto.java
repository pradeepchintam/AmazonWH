package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class CycleCountDto {
    private Long id;
    private Long dcId;
    private Long locationId;
    private String locationCode;
    private Long skuId;
    private String skuCode;
    private String status;
    private Integer systemQty;
    private Integer countedQty;
    private Integer variance;
    private String countedAt;
    private String notes;
}

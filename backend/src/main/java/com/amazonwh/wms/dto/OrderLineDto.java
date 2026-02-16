package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class OrderLineDto {
    private Long id;
    private Long skuId;
    private String skuCode;
    private Integer lineNumber;
    private Integer orderedQty;
    private Integer allocatedQty;
    private Integer pickedQty;
    private String status;
}

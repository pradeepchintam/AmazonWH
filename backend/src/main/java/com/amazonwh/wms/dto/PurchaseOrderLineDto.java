package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class PurchaseOrderLineDto {
    private Long id;
    private Long skuId;
    private String skuCode;
    private Integer expectedQty;
    private Integer receivedQty;
    private Integer lineNumber;
    private String status;
}

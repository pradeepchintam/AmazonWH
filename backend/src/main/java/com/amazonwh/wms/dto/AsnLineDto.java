package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class AsnLineDto {
    private Long id;
    private Long poLineId;
    private Long skuId;
    private String skuCode;
    private Integer expectedQty;
    private Integer receivedQty;
    private String lotNumber;
    private String status;
}

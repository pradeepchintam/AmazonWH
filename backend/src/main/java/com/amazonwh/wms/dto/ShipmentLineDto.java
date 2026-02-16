package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class ShipmentLineDto {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String status;
}

package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class PickTaskDto {
    private Long id;
    private Long waveId;
    private String waveNumber;
    private Long orderId;
    private String orderNumber;
    private Long orderLineId;
    private Long skuId;
    private String skuCode;
    private String skuDescription;
    private Long fromLocationId;
    private String fromLocationCode;
    private Long dcId;
    private Integer qtyToPick;
    private Integer qtyPicked;
    private String status;
    private Long assignedTo;
    private String assignedToName;
    private Integer pickSequence;
}

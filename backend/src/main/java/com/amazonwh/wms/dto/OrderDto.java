package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long dcId;
    private String status;
    private Integer priority;
    private String customerName;
    private String shipToAddress;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private Long carrierId;
    private String carrierName;
    private Long waveId;
    private String waveNumber;
    private String requiredDate;
    private String notes;
    private List<OrderLineDto> lines;
}

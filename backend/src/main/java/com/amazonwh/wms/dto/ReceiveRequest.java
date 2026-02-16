package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class ReceiveRequest {
    private Long asnLineId;
    private Integer qty;
    private Long locationId;
    private String lotNumber;
}

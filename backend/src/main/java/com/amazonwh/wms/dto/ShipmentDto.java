package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class ShipmentDto {
    private Long id;
    private String shipmentNumber;
    private Long dcId;
    private Long carrierId;
    private String carrierName;
    private String status;
    private String bolNumber;
    private String trailerNumber;
    private String doorNumber;
    private String shippedAt;
    private String notes;
    private List<ShipmentLineDto> lines;
}

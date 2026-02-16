package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class AsnDto {
    private Long id;
    private String asnNumber;
    private Long poId;
    private String poNumber;
    private Long carrierId;
    private String carrierName;
    private String status;
    private String expectedArrival;
    private String arrivedAt;
    private String trailerNumber;
    private String doorNumber;
    private String notes;
    private List<AsnLineDto> lines;
}

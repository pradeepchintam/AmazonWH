package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class LocationDto {
    private Long id;
    private Long dcId;
    private String dcCode;
    private String code;
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String position;
    private String locType;
    private Integer maxQty;
    private Integer pickSequence;
    private Boolean active;
}

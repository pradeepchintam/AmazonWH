package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class CarrierDto {
    private Long id;
    private String code;
    private String name;
    private String scac;
    private String contactName;
    private String phone;
    private Boolean active;
}

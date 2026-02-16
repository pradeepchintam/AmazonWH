package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class DCDto {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private Boolean active;
}

package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class VendorDto {
    private Long id;
    private String code;
    private String name;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private Boolean active;
}

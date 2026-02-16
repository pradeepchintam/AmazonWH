package com.amazonwh.wms.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuDto {
    private Long id;
    private String code;
    private String description;
    private String uom;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private Boolean active;
}

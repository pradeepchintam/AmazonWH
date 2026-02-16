package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class WaveCreateRequest {
    private Long dcId;
    private List<Long> orderIds;
}

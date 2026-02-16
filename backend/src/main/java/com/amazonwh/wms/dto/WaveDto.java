package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class WaveDto {
    private Long id;
    private String waveNumber;
    private Long dcId;
    private String status;
    private String releasedAt;
    private String completedAt;
    private Integer orderCount;
    private List<Long> orderIds;
}

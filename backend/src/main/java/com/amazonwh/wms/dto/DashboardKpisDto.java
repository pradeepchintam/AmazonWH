package com.amazonwh.wms.dto;

import lombok.Data;

@Data
public class DashboardKpisDto {
    private Long totalInventory;
    private Long openOrders;
    private Long pendingPicks;
    private Long shipmentsToday;
    private Long inboundToday;
    private Double pickCompletionRate;
}

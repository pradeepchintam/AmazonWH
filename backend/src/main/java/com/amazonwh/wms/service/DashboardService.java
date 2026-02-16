package com.amazonwh.wms.service;

import com.amazonwh.wms.dto.DashboardKpisDto;
import com.amazonwh.wms.model.enums.*;
import com.amazonwh.wms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InventoryRepository invRepo;
    private final OrderRepository orderRepo;
    private final PickTaskRepository pickRepo;
    private final ShipmentRepository shipRepo;
    private final AsnRepository asnRepo;

    public DashboardKpisDto getKpis(Long dcId) {
        Long dc = dcId != null ? dcId : 1L;
        DashboardKpisDto kpis = new DashboardKpisDto();
        Long total = invRepo.totalQty(dc);
        kpis.setTotalInventory(total != null ? total : 0L);
        kpis.setOpenOrders(orderRepo.countByDcIdAndStatusIn(dc, List.of(OrderStatus.CREATED, OrderStatus.ALLOCATED, OrderStatus.PICKING)));
        kpis.setPendingPicks(pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.PENDING));
        kpis.setShipmentsToday(shipRepo.countByDcIdAndShippedAtAfter(dc, LocalDate.now().atStartOfDay()));

        long totalPicks = pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.COMPLETED) + pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.PENDING)
                + pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.IN_PROGRESS) + pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.ASSIGNED);
        long completedPicks = pickRepo.countByDcIdAndStatus(dc, PickTaskStatus.COMPLETED);
        kpis.setPickCompletionRate(totalPicks > 0 ? Math.round((double) completedPicks / totalPicks * 100.0) : 0.0);
        kpis.setInboundToday(0L); // simplified
        return kpis;
    }

    public List<Map<String, Object>> getInventoryByStatus(Long dcId) {
        Long dc = dcId != null ? dcId : 1L;
        List<Object[]> results = invRepo.sumByStatus(dc);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", row[0].toString());
            m.put("value", ((Number) row[1]).longValue());
            data.add(m);
        }
        return data;
    }

    public List<Map<String, Object>> getInboundTrend(Long dcId) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> m = new HashMap<>();
            m.put("date", LocalDate.now().minusDays(i).toString());
            m.put("qty", 0);
            data.add(m);
        }
        return data;
    }

    public List<Map<String, Object>> getPickPerformance(Long dcId) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> m = new HashMap<>();
            m.put("date", LocalDate.now().minusDays(i).toString());
            m.put("completed", 0);
            m.put("total", 0);
            data.add(m);
        }
        return data;
    }
}

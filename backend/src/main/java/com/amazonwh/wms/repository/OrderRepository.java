package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.WmsOrder;
import com.amazonwh.wms.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<WmsOrder, Long> {
    Optional<WmsOrder> findByOrderNumber(String orderNumber);
    Page<WmsOrder> findByDcId(Long dcId, Pageable pageable);
    Page<WmsOrder> findByDcIdAndStatus(Long dcId, OrderStatus status, Pageable pageable);
    long countByDcIdAndStatusIn(Long dcId, java.util.List<OrderStatus> statuses);
}

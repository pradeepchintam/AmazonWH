package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.PurchaseOrder;
import com.amazonwh.wms.model.enums.PoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    Page<PurchaseOrder> findByDcId(Long dcId, Pageable pageable);
    Page<PurchaseOrder> findByDcIdAndStatus(Long dcId, PoStatus status, Pageable pageable);
}

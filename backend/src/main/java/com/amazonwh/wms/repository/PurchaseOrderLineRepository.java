package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, Long> {
    List<PurchaseOrderLine> findByPurchaseOrderId(Long poId);
}

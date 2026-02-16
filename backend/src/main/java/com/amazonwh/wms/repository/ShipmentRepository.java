package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Page<Shipment> findByDcId(Long dcId, Pageable pageable);
    long countByDcIdAndShippedAtAfter(Long dcId, LocalDateTime after);
}

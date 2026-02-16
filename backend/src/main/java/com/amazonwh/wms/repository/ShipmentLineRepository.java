package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.ShipmentLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentLineRepository extends JpaRepository<ShipmentLine, Long> {
    List<ShipmentLine> findByShipmentId(Long shipmentId);
}

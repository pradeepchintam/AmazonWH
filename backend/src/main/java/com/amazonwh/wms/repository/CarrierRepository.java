package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Optional<Carrier> findByCode(String code);
}

package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SkuRepository extends JpaRepository<Sku, Long> {
    Optional<Sku> findByCode(String code);
}

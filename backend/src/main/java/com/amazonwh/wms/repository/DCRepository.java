package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.DC;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DCRepository extends JpaRepository<DC, Long> {
    Optional<DC> findByCode(String code);
}

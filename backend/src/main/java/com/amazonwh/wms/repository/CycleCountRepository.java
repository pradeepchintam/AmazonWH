package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.CycleCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleCountRepository extends JpaRepository<CycleCount, Long> {
    Page<CycleCount> findByDcId(Long dcId, Pageable pageable);
}

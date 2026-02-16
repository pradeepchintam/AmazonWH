package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Wave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaveRepository extends JpaRepository<Wave, Long> {
    Page<Wave> findByDcId(Long dcId, Pageable pageable);
}

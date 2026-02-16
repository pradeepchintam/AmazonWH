package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByDcIdAndCode(Long dcId, String code);
    Page<Location> findByDcId(Long dcId, Pageable pageable);
    List<Location> findByDcIdAndZone(Long dcId, String zone);
}

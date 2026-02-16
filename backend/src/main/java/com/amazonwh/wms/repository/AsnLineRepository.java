package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.AsnLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AsnLineRepository extends JpaRepository<AsnLine, Long> {
    List<AsnLine> findByAsnId(Long asnId);
}

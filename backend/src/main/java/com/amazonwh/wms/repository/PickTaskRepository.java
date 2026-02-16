package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.PickTask;
import com.amazonwh.wms.model.enums.PickTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PickTaskRepository extends JpaRepository<PickTask, Long> {
    List<PickTask> findByWaveId(Long waveId);
    List<PickTask> findByAssignedToIdAndStatusIn(Long userId, List<PickTaskStatus> statuses);
    Page<PickTask> findByDcId(Long dcId, Pageable pageable);
    long countByDcIdAndStatus(Long dcId, PickTaskStatus status);
    List<PickTask> findByOrderLineId(Long orderLineId);
}

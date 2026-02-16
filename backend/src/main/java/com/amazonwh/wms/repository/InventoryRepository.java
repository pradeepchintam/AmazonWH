package com.amazonwh.wms.repository;

import com.amazonwh.wms.model.Inventory;
import com.amazonwh.wms.model.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuIdAndLocationIdAndStatusAndLotNumber(Long skuId, Long locationId, InventoryStatus status, String lotNumber);
    Page<Inventory> findByDcId(Long dcId, Pageable pageable);
    List<Inventory> findBySkuIdAndDcIdAndStatus(Long skuId, Long dcId, InventoryStatus status);

    @Query("SELECT i FROM Inventory i JOIN i.sku s JOIN i.location l WHERE i.dc.id = :dcId " +
           "AND (:skuCode IS NULL OR s.code LIKE %:skuCode%) " +
           "AND (:locationCode IS NULL OR l.code LIKE %:locationCode%) " +
           "AND (:status IS NULL OR i.status = :status)")
    Page<Inventory> search(@Param("dcId") Long dcId, @Param("skuCode") String skuCode,
                           @Param("locationCode") String locationCode, @Param("status") InventoryStatus status, Pageable pageable);

    @Query("SELECT i.status as status, SUM(i.qtyOnHand) as total FROM Inventory i WHERE i.dc.id = :dcId GROUP BY i.status")
    List<Object[]> sumByStatus(@Param("dcId") Long dcId);

    @Query("SELECT SUM(i.qtyOnHand) FROM Inventory i WHERE i.dc.id = :dcId")
    Long totalQty(@Param("dcId") Long dcId);

    List<Inventory> findByLocationId(Long locationId);
}

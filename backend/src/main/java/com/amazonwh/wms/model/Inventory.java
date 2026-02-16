package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "inventory")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Enumerated(EnumType.STRING) @Builder.Default private InventoryStatus status = InventoryStatus.AVAILABLE;
    @Column(name = "lot_number") private String lotNumber;
    @Column(name = "qty_on_hand") @Builder.Default private Integer qtyOnHand = 0;
    @Column(name = "qty_allocated") @Builder.Default private Integer qtyAllocated = 0;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

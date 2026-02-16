package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.CycleCountStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "cycle_count")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CycleCount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id")
    private Sku sku;
    @Enumerated(EnumType.STRING) @Builder.Default private CycleCountStatus status = CycleCountStatus.PENDING;
    @Column(name = "system_qty") private Integer systemQty;
    @Column(name = "counted_qty") private Integer countedQty;
    private Integer variance;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "counted_by")
    private AppUser countedBy;
    @Column(name = "counted_at") private LocalDateTime countedAt;
    private String notes;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

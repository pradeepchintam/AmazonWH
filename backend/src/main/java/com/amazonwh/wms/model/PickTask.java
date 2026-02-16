package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.PickTaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "pick_task")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PickTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "wave_id", nullable = false)
    private Wave wave;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false)
    private WmsOrder order;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_line_id", nullable = false)
    private OrderLine orderLine;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Column(name = "qty_to_pick", nullable = false) private Integer qtyToPick;
    @Column(name = "qty_picked") @Builder.Default private Integer qtyPicked = 0;
    @Enumerated(EnumType.STRING) @Builder.Default private PickTaskStatus status = PickTaskStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;
    @Column(name = "pick_sequence") @Builder.Default private Integer pickSequence = 0;
    @Column(name = "started_at") private LocalDateTime startedAt;
    @Column(name = "completed_at") private LocalDateTime completedAt;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

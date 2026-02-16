package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.OrderLineStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "order_line")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false)
    private WmsOrder order;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;
    @Column(name = "line_number", nullable = false) private Integer lineNumber;
    @Column(name = "ordered_qty", nullable = false) private Integer orderedQty;
    @Column(name = "allocated_qty") @Builder.Default private Integer allocatedQty = 0;
    @Column(name = "picked_qty") @Builder.Default private Integer pickedQty = 0;
    @Enumerated(EnumType.STRING) @Builder.Default private OrderLineStatus status = OrderLineStatus.OPEN;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.PoLineStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "purchase_order_line")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;
    @Column(name = "expected_qty", nullable = false) private Integer expectedQty;
    @Column(name = "received_qty") @Builder.Default private Integer receivedQty = 0;
    @Column(name = "line_number", nullable = false) private Integer lineNumber;
    @Enumerated(EnumType.STRING)
    @Builder.Default private PoLineStatus status = PoLineStatus.OPEN;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

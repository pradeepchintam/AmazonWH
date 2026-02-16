package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.AsnLineStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "asn_line")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AsnLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "asn_id", nullable = false)
    private Asn asn;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "po_line_id", nullable = false)
    private PurchaseOrderLine poLine;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;
    @Column(name = "expected_qty", nullable = false) private Integer expectedQty;
    @Column(name = "received_qty") @Builder.Default private Integer receivedQty = 0;
    @Column(name = "lot_number") private String lotNumber;
    @Enumerated(EnumType.STRING) @Builder.Default private AsnLineStatus status = AsnLineStatus.PENDING;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

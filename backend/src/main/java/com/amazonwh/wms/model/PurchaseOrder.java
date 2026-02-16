package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.PoStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "purchase_order")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "po_number", nullable = false, unique = true, length = 30)
    private String poNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Enumerated(EnumType.STRING)
    @Builder.Default private PoStatus status = PoStatus.CREATED;
    @Column(name = "expected_date") private LocalDate expectedDate;
    private String notes;
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<PurchaseOrderLine> lines = new ArrayList<>();
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

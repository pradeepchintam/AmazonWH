package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.AsnStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "asn")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Asn {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "asn_number", nullable = false, unique = true, length = 30)
    private String asnNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @Enumerated(EnumType.STRING) @Builder.Default private AsnStatus status = AsnStatus.CREATED;
    @Column(name = "expected_arrival") private LocalDate expectedArrival;
    @Column(name = "arrived_at") private LocalDateTime arrivedAt;
    @Column(name = "trailer_number") private String trailerNumber;
    @Column(name = "door_number") private String doorNumber;
    private String notes;
    @OneToMany(mappedBy = "asn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<AsnLine> lines = new ArrayList<>();
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

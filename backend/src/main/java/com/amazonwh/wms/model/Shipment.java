package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "shipment")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "shipment_number", nullable = false, unique = true, length = 30)
    private String shipmentNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @Enumerated(EnumType.STRING) @Builder.Default private ShipmentStatus status = ShipmentStatus.CREATED;
    @Column(name = "bol_number") private String bolNumber;
    @Column(name = "trailer_number") private String trailerNumber;
    @Column(name = "door_number") private String doorNumber;
    @Column(name = "shipped_at") private LocalDateTime shippedAt;
    private String notes;
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<ShipmentLine> lines = new ArrayList<>();
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "wms_order")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class WmsOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_number", nullable = false, unique = true, length = 30)
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Enumerated(EnumType.STRING) @Builder.Default private OrderStatus status = OrderStatus.CREATED;
    @Builder.Default private Integer priority = 5;
    @Column(name = "customer_name") private String customerName;
    @Column(name = "ship_to_address") private String shipToAddress;
    @Column(name = "ship_to_city") private String shipToCity;
    @Column(name = "ship_to_state") private String shipToState;
    @Column(name = "ship_to_zip") private String shipToZip;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "wave_id")
    private Wave wave;
    @Column(name = "required_date") private LocalDate requiredDate;
    private String notes;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<OrderLine> lines = new ArrayList<>();
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.amazonwh.wms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "location")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Location {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Column(nullable = false, length = 30)
    private String code;
    @Column(nullable = false, length = 20)
    private String zone;
    private String aisle;
    private String bay;
    private String level;
    private String position;
    @Column(name = "loc_type")
    @Builder.Default private String locType = "STORAGE";
    @Column(name = "max_qty")
    @Builder.Default private Integer maxQty = 9999;
    @Column(name = "pick_sequence")
    @Builder.Default private Integer pickSequence = 0;
    @Builder.Default private Boolean active = true;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

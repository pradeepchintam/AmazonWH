package com.amazonwh.wms.model;

import com.amazonwh.wms.model.enums.WaveStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "wave")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Wave {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "wave_number", nullable = false, unique = true, length = 30)
    private String waveNumber;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "dc_id", nullable = false)
    private DC dc;
    @Enumerated(EnumType.STRING) @Builder.Default private WaveStatus status = WaveStatus.CREATED;
    @Column(name = "released_at") private LocalDateTime releasedAt;
    @Column(name = "completed_at") private LocalDateTime completedAt;
    @OneToMany(mappedBy = "wave") @Builder.Default private List<WmsOrder> orders = new ArrayList<>();
    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.bocklercode.cosmos_odyssey_core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID startId;

    @Column(nullable = false)
    private UUID endId;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private BigDecimal totalDistance;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private Long totalDuration;
}

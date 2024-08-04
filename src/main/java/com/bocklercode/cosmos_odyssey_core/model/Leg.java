package com.bocklercode.cosmos_odyssey_core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;  // Lisa see import
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "legs")
public class Leg {

    @Id
    @Column(name = "leg_id")
    private UUID legId;

    @Column(unique = true)
    private UUID routeId;

    @Column(nullable = false)
    private UUID fromId;

    @Column(nullable = false, length = 50)
    private String fromName;

    @Column(nullable = false)
    private UUID toId;

    @Column(nullable = false, length = 50)
    private String toName;

    @Column(nullable = false)
    private Long distance;

    @ManyToOne
    @JoinColumn(name = "pricelist_id", nullable = false)
    private PriceList pricelist;
}

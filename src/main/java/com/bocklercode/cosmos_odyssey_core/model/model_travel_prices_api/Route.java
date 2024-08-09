package com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @Column(name = "route_id")
    private UUID routeId;

    @Column(name = "from_id", nullable = false)
    private UUID fromId;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_id", nullable = false)
    private UUID toId;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "distance", nullable = false)
    private long distance;

    @OneToOne
    @JoinColumn(name = "leg_id", nullable = false, unique = true)
    private Leg leg;
}

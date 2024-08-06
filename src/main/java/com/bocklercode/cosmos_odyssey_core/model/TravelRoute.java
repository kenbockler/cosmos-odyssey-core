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
@Table(name = "travel_routes")
public class TravelRoute {

    @Id
    @Column(name = "travel_route_id")
    private UUID travelRouteId;

    @Column(name = "from_id", nullable = false)
    private UUID fromId;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_id", nullable = false)
    private UUID toId;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "total_quoted_travel_time", nullable = false)
    private long totalQuotedTravelTime;

    @Column(name = "total_quoted_price", nullable = false)
    private BigDecimal totalQuotedPrice;

    @Column(name = "total_quoted_duration", nullable = false)
    private long totalQuotedDuration;
}

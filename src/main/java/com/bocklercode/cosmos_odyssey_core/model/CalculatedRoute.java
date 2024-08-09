package com.bocklercode.cosmos_odyssey_core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "calculated_routes")
public class CalculatedRoute {

    @Id
    @Column(name = "calculated_route_id")
    private UUID calculatedRouteId;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "company_names", nullable = false, columnDefinition = "TEXT")
    private String companyNames;

    @Column(name = "total_quoted_price", nullable = false)
    private BigDecimal totalQuotedPrice;

    @Column(name = "total_trip_start", nullable = false)
    private Instant totalTripStart;

    @Column(name = "total_trip_end", nullable = false)
    private Instant totalTripEnd;

    @Column(name = "total_quoted_travel_time", nullable = false)
    private long totalQuotedTravelTime;

    @Column(name = "total_quoted_distance", nullable = false)
    private long totalQuotedDistance;
}

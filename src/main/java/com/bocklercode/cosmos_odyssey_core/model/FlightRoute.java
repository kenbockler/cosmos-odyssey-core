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
@Table(name = "flight_routes")
public class FlightRoute {

    @Id
    @Column(name = "flight_route_id", nullable = false)
    private UUID id;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "flight_start", nullable = false)
    private Instant flightStart;

    @Column(name = "flight_end", nullable = false)
    private Instant flightEnd;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "total_quoted_price", nullable = false)
    private BigDecimal totalQuotedPrice;

    @Column(name = "total_quoted_travel_time", nullable = false)
    private long totalQuotedTravelTime;

    @Column(name = "total_quoted_distance", nullable = false)
    private long totalQuotedDistance;
}

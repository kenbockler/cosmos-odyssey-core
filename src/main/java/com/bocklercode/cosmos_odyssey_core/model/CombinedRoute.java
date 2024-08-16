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
@Table(name = "combined_routes")
public class CombinedRoute {

    @Id
    @Column(name = "combined_route_id", nullable = false)
    private UUID combinedRouteId;

    @Column(name = "from_name", nullable = false)
    private String fromName;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "route", nullable = false, columnDefinition = "TEXT")
    private String route;

    @Column(name = "first_flight_start", nullable = false)
    private Instant firstFlightStart;

    @Column(name = "last_flight_end", nullable = false)
    private Instant lastFlightEnd;

    @Column(name = "company_names", nullable = false, columnDefinition = "TEXT")
    private String companyNames;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_travel_time", nullable = false)
    private long totalTravelTime;

    @Column(name = "total_distance", nullable = false)
    private long totalDistance;

    @Column(name = "price_list_id", nullable = false)
    private UUID priceListId;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;
}
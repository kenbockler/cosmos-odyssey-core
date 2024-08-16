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
@Table(name = "provider_leg_route_combined")
public class ProviderLegRouteCombined {

    @Id
    @Column(name = "provider_id")
    private UUID providerId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "flight_start", nullable = false)
    private Instant flightStart;

    @Column(name = "flight_end", nullable = false)
    private Instant flightEnd;

    @Column(name = "duration", nullable = false)
    private long duration;

    @Column(name = "leg_id", nullable = false)
    private UUID legId;

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

    @Column(name = "price_list_id", nullable = false)
    private UUID priceListId;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;
}

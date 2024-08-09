package com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api;

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
@Table(name = "providers")
public class Provider {

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

    @ManyToOne
    @JoinColumn(name = "leg_id", nullable = false)
    private Leg leg;
}

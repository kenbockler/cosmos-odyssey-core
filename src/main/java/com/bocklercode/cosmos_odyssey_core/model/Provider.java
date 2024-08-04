package com.bocklercode.cosmos_odyssey_core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
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
    @Column(name = "provider_id", nullable = false)
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "flight_start", nullable = false)
    private Instant flightStart;

    @Column(name = "flight_end", nullable = false)
    private Instant flightEnd;

    @ManyToOne
    @JoinColumn(name = "leg_id", nullable = false)
    private Leg leg;

    public long getDuration() {
        return Duration.between(flightStart, flightEnd).toMinutes(); // Kestvuse arvutamine minutites
    }
}

package com.bocklercode.cosmos_odyssey_core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pricelists")
public class PriceList {

    @Id
    @Column(name = "pricelist_id")
    private UUID id;

    @Column(nullable = false)
    private Instant validUntil;

    @Column(nullable = false)
    private Instant createdAt;
}

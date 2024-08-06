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
@Table(name = "price_lists")
public class PriceList {

    @Id
    @Column(name = "price_list_id")
    private UUID priceListId;

    @Column(nullable = false)
    private Instant validUntil;
}

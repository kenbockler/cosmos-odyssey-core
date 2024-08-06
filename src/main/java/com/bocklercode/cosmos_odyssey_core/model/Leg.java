package com.bocklercode.cosmos_odyssey_core.model;

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
@Table(name = "legs")
public class Leg {

    @Id
    @Column(name = "leg_id")
    private UUID legId;

    @ManyToOne
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;
}

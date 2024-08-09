package com.bocklercode.cosmos_odyssey_core.model;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.PriceList;
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
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    private UUID reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_quoted_price", nullable = false)
    private BigDecimal totalQuotedPrice;

    @Column(name = "total_quoted_travel_time", nullable = false)
    private long totalQuotedTravelTime;

    @ManyToOne
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;
}

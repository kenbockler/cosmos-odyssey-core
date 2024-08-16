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
    @GeneratedValue
    @Column(name = "reservation_id")
    private UUID reservationId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "route", nullable = false)
    private String route;

    @Column(name = "total_quoted_price", nullable = false)
    private BigDecimal totalQuotedPrice;

    @Column(name = "total_quoted_travel_time", nullable = false)
    private BigDecimal totalQuotedTravelTime;

    @Column(name = "company_names", nullable = false)
    private String companyNames;

    @ManyToOne
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;
}

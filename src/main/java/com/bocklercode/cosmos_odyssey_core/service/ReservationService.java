package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.Reservation;
import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.PriceList;
import com.bocklercode.cosmos_odyssey_core.repository.ReservationRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PriceListRepository priceListRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, PriceListRepository priceListRepository) {
        this.reservationRepository = reservationRepository;
        this.priceListRepository = priceListRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        UUID priceListId = reservation.getPriceList().getPriceListId();

        Optional<PriceList> priceListOpt = priceListRepository.findById(priceListId);
        if (priceListOpt.isPresent()) {
            PriceList priceList = priceListOpt.get();
            if (!priceList.isActive()) {
                throw new IllegalArgumentException("This travel offer has expired. New offers will be loaded for you in a moment.");
            }
        } else {
            throw new IllegalArgumentException("This travel offer has expired. New offers will be loaded for you in a moment.");
        }

        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }
}

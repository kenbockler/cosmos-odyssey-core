package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.Reservation;
import com.bocklercode.cosmos_odyssey_core.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        reservation.setReservationId(UUID.randomUUID());
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId).orElse(null);
    }

    public List<Reservation> getReservationsByUserId(UUID userId) {
        return reservationRepository.findByUser_UserId(userId);
    }

    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(UUID reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}

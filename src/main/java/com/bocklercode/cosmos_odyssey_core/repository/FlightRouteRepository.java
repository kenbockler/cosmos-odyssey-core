package com.bocklercode.cosmos_odyssey_core.repository;

import com.bocklercode.cosmos_odyssey_core.model.FlightRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlightRouteRepository extends JpaRepository<FlightRoute, UUID> {
}

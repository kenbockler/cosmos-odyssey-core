package com.bocklercode.cosmos_odyssey_core.repository;

import com.bocklercode.cosmos_odyssey_core.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {
    Optional<Route> findByLeg_LegId(UUID legId);
}

package com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.Leg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LegRepository extends JpaRepository<Leg, UUID> {
}
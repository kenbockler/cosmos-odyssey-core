package com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    List<Provider> findByLeg_LegId(UUID legId);
}
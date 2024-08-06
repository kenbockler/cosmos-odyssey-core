package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.TravelRoute;

import java.util.List;
import java.util.UUID;

public interface TravelRouteService {
    void calculateAndSaveTravelRoutes();
    List<TravelRoute> findRoutes(UUID fromId, UUID toId);
}

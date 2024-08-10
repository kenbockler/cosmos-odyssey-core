package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.FlightRoute;
import com.bocklercode.cosmos_odyssey_core.model.ProviderLegRouteCombined;
import com.bocklercode.cosmos_odyssey_core.repository.FlightRouteRepository;
import com.bocklercode.cosmos_odyssey_core.repository.ProviderLegRouteCombinedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Service class responsible for managing flight route data
@Service
public class FlightRouteService {

    // Repository for accessing FlightRoute data
    private final FlightRouteRepository flightRouteRepository;
    // Repository for accessing combined route data from different providers
    private final ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository;

    // Constructor
    @Autowired
    public FlightRouteService(FlightRouteRepository flightRouteRepository,
                              ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository) {
        this.flightRouteRepository = flightRouteRepository;
        this.providerLegRouteCombinedRepository = providerLegRouteCombinedRepository;
    }

    // Method to save flight routes into the database
    public void saveFlightRoutes() {
        // Retrieve all combined routes data from the repository
        List<ProviderLegRouteCombined> combinedRoutes = providerLegRouteCombinedRepository.findAll();

        // Transform combined route data into flight route entities and persist them
        List<FlightRoute> flightRoutes = combinedRoutes.stream()
                .map(this::convertToFlightRoute)
                .collect(Collectors.toList());

        // Save all flight routes in the database
        flightRouteRepository.saveAll(flightRoutes);

        System.out.println("Flight routes saved successfully.");
    }

    // Method to check if flight routes have been loaded into the database
    boolean isFlightRoutesLoaded() {
        // Check the count of flight routes to determine if any are loaded
        return flightRouteRepository.count() > 0;
    }

    // Private helper method to convert a combined route from a provider to a FlightRoute entity
    private FlightRoute convertToFlightRoute(ProviderLegRouteCombined providerLegRoute) {
        return FlightRoute.builder()
                .id(UUID.randomUUID())
                .fromName(providerLegRoute.getFromName())
                .toName(providerLegRoute.getToName())
                .flightStart(providerLegRoute.getFlightStart())
                .flightEnd(providerLegRoute.getFlightEnd())
                .companyName(providerLegRoute.getCompanyName())
                .totalQuotedPrice(providerLegRoute.getPrice())
                .totalQuotedTravelTime(providerLegRoute.getDuration())
                .totalQuotedDistance(providerLegRoute.getDistance())
                .build();
    }
}

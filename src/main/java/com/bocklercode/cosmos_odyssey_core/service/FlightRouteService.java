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

@Service
public class FlightRouteService {

    private final FlightRouteRepository flightRouteRepository;
    private final ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository;

    @Autowired
    public FlightRouteService(FlightRouteRepository flightRouteRepository,
                              ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository) {
        this.flightRouteRepository = flightRouteRepository;
        this.providerLegRouteCombinedRepository = providerLegRouteCombinedRepository;
    }

    public void saveFlightRoutes() {
        // Loe andmed ühendatud tabelist
        List<ProviderLegRouteCombined> combinedRoutes = providerLegRouteCombinedRepository.findAll();

        // Teisenda ja salvesta andmed vastavas järjekorras
        List<FlightRoute> flightRoutes = combinedRoutes.stream()
                .map(this::convertToFlightRoute)
                .collect(Collectors.toList());

        flightRouteRepository.saveAll(flightRoutes);

        System.out.println("Flight routes saved successfully.");
    }

    private FlightRoute convertToFlightRoute(ProviderLegRouteCombined providerLegRoute) {
        return FlightRoute.builder()
                .id(UUID.randomUUID())  // Genereerime uue UUID
                .fromName(providerLegRoute.getFromName())
                .toName(providerLegRoute.getToName())
                .flightStart(providerLegRoute.getFlightStart())
                .flightEnd(providerLegRoute.getFlightEnd())
                .companyName(providerLegRoute.getCompanyName())
                .totalQuotedPrice(providerLegRoute.getPrice()) // Kaardistab `price` totalQuotedPrice'iks
                .totalQuotedTravelTime(providerLegRoute.getDuration()) // Kaardistab `duration` totalQuotedTravelTime'iks
                .totalQuotedDistance(providerLegRoute.getDistance()) // Kaardistab `distance` totalQuotedDistance'iks
                .build();
    }
}

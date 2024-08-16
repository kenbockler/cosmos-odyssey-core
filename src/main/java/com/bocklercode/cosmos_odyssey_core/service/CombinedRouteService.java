package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.CombinedRoute;
import com.bocklercode.cosmos_odyssey_core.model.ProviderLegRouteCombined;
import com.bocklercode.cosmos_odyssey_core.repository.CombinedRouteRepository;
import com.bocklercode.cosmos_odyssey_core.repository.ProviderLegRouteCombinedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CombinedRouteService {

    private final ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository;
    private final CombinedRouteRepository combinedRouteRepository;

    @Autowired
    public CombinedRouteService(ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository,
                                CombinedRouteRepository combinedRouteRepository) {
        this.providerLegRouteCombinedRepository = providerLegRouteCombinedRepository;
        this.combinedRouteRepository = combinedRouteRepository;
    }

    public void generateAndSaveRouteCombinations() {
        if (combinedRouteRepository.count() > 0) {
            combinedRouteRepository.deleteAll();
            System.out.println("Existing combined routes deleted.");
        }

        List<ProviderLegRouteCombined> allRoutes = providerLegRouteCombinedRepository.findAll();

        for (ProviderLegRouteCombined initialRoute : allRoutes) {
            List<CombinedRoute> combinations = generateCombinations(initialRoute, allRoutes);
            combinedRouteRepository.saveAll(combinations);
        }

        System.out.println("Combined routes saved successfully.");
    }

    private List<CombinedRoute> generateCombinations(ProviderLegRouteCombined startRoute, List<ProviderLegRouteCombined> allRoutes) {
        List<CombinedRoute> results = new ArrayList<>();
        generateCombinationsRecursive(startRoute, allRoutes, new ArrayList<>(), results, new HashSet<>());
        return results;
    }

    private void generateCombinationsRecursive(ProviderLegRouteCombined currentRoute, List<ProviderLegRouteCombined> allRoutes,
                                               List<ProviderLegRouteCombined> currentCombination, List<CombinedRoute> results,
                                               Set<String> visitedLocations) {
        if (!visitedLocations.add(currentRoute.getFromName())) {
            return;
        }

        currentCombination.add(currentRoute);

        List<ProviderLegRouteCombined> nextRoutes = allRoutes.stream()
                .filter(route -> route.getFromName().equals(currentRoute.getToName()))
                .filter(route -> !route.getFlightStart().isBefore(currentRoute.getFlightEnd()))
                .collect(Collectors.toList());

        if (nextRoutes.isEmpty()) {
            results.add(createCombinedRoute(currentCombination));
        } else {
            for (ProviderLegRouteCombined nextRoute : nextRoutes) {
                generateCombinationsRecursive(nextRoute, allRoutes, new ArrayList<>(currentCombination), results, new HashSet<>(visitedLocations));
            }
        }
    }

    private CombinedRoute createCombinedRoute(List<ProviderLegRouteCombined> routes) {
        UUID combinedRouteId = UUID.randomUUID();
        String fromName = routes.get(0).getFromName();
        String toName = routes.get(routes.size() - 1).getToName();
        String route = routes.stream().map(ProviderLegRouteCombined::getFromName).distinct().collect(Collectors.joining(", ")) + ", " + toName;

        Instant firstFlightStart = routes.get(0).getFlightStart();
        Instant lastFlightEnd = routes.get(routes.size() - 1).getFlightEnd();
        String companyNames = routes.stream().map(ProviderLegRouteCombined::getCompanyName).collect(Collectors.joining(", "));
        BigDecimal totalPrice = routes.stream().map(ProviderLegRouteCombined::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalTravelTime = routes.stream().mapToLong(ProviderLegRouteCombined::getDuration).sum();
        long totalDistance = routes.stream().mapToLong(ProviderLegRouteCombined::getDistance).sum();
        UUID priceListId = routes.get(0).getPriceListId();
        Instant validUntil = routes.get(0).getValidUntil();

        return CombinedRoute.builder()
                .combinedRouteId(combinedRouteId)
                .fromName(fromName)
                .toName(toName)
                .route(route)
                .firstFlightStart(firstFlightStart)
                .lastFlightEnd(lastFlightEnd)
                .companyNames(companyNames)
                .totalPrice(totalPrice)
                .totalTravelTime(totalTravelTime)
                .totalDistance(totalDistance)
                .priceListId(priceListId)
                .validUntil(validUntil)
                .build();
    }
}

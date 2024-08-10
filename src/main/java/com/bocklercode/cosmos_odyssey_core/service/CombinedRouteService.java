package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.CombinedRoute;
import com.bocklercode.cosmos_odyssey_core.model.FlightRoute;
import com.bocklercode.cosmos_odyssey_core.repository.CombinedRouteRepository;
import com.bocklercode.cosmos_odyssey_core.repository.FlightRouteRepository;
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

    private final FlightRouteRepository flightRouteRepository;
    private final CombinedRouteRepository combinedRouteRepository;

    @Autowired
    public CombinedRouteService(FlightRouteRepository flightRouteRepository,
                                CombinedRouteRepository combinedRouteRepository) {
        this.flightRouteRepository = flightRouteRepository;
        this.combinedRouteRepository = combinedRouteRepository;
    }

    public void generateAndSaveRouteCombinations() {
        List<FlightRoute> allRoutes = flightRouteRepository.findAll();

        for (FlightRoute initialRoute : allRoutes) {
            List<CombinedRoute> combinations = generateCombinations(initialRoute, allRoutes);
            combinedRouteRepository.saveAll(combinations);
        }
    }

    private List<CombinedRoute> generateCombinations(FlightRoute startRoute, List<FlightRoute> allRoutes) {
        List<CombinedRoute> results = new ArrayList<>();
        generateCombinationsRecursive(startRoute, allRoutes, new ArrayList<>(), results, new HashSet<>());
        return results;
    }

    private void generateCombinationsRecursive(FlightRoute currentRoute, List<FlightRoute> allRoutes,
                                               List<FlightRoute> currentCombination, List<CombinedRoute> results,
                                               Set<String> visitedLocations) {
        // Lisame praeguse teekonna nime külastatud asukohtade hulka
        if (!visitedLocations.add(currentRoute.getFromName())) {
            return; // Kui see asukoht on juba külastatud, lõpetame rekursiooni
        }

        currentCombination.add(currentRoute);

        // Leia kõik järgmised lennud, mis algavad praeguse teekonna lõpp-punktist
        List<FlightRoute> nextRoutes = allRoutes.stream()
                .filter(route -> route.getFromName().equals(currentRoute.getToName()))
                .filter(route -> !route.getFlightStart().isBefore(currentRoute.getFlightEnd())) // Kontroll, et järgmine lend ei alga enne eelmist
                .collect(Collectors.toList());

        if (nextRoutes.isEmpty()) {
            // Kui teekonna lõpp-punkti ei ole rohkem jätkuvaid lende, salvestame kombinatsiooni
            results.add(createCombinedRoute(currentCombination));
        } else {
            for (FlightRoute nextRoute : nextRoutes) {
                generateCombinationsRecursive(nextRoute, allRoutes, new ArrayList<>(currentCombination), results, new HashSet<>(visitedLocations));
            }
        }
    }

    private CombinedRoute createCombinedRoute(List<FlightRoute> flightRoutes) {
        UUID combinedRouteId = UUID.randomUUID();
        String fromName = flightRoutes.get(0).getFromName();
        String toName = flightRoutes.get(flightRoutes.size() - 1).getToName();

        // Muudame teekonna loogikat, et see kuvaks pidevat stringi ilma liigsete tühikuteta ja eraldajatega
        String route = flightRoutes.stream()
                .map(FlightRoute::getFromName)
                .distinct()  // Väldime duplikaatide kuvamist
                .collect(Collectors.joining("-->"))
                + "-->" + toName; // Lisame lõpp-punkti, et kuvada täielik teekond

        Instant firstFlightStart = flightRoutes.get(0).getFlightStart();
        Instant lastFlightEnd = flightRoutes.get(flightRoutes.size() - 1).getFlightEnd();
        String companyNames = flightRoutes.stream()
                .map(FlightRoute::getCompanyName)
                .collect(Collectors.joining(", "));
        BigDecimal totalPrice = flightRoutes.stream()
                .map(FlightRoute::getTotalQuotedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalTravelTime = flightRoutes.stream()
                .mapToLong(FlightRoute::getTotalQuotedTravelTime)
                .sum();
        long totalDistance = flightRoutes.stream()
                .mapToLong(FlightRoute::getTotalQuotedDistance)
                .sum();

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
                .build();
    }
}

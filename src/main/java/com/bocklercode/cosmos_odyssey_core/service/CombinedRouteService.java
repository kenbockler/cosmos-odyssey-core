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

// This service calculates and saves all possible route combinations into the combined_routes table
@Service
public class CombinedRouteService {

    // The FlightRouteRepository is used to get all possible routes
    private final FlightRouteRepository flightRouteRepository;
    // The CombinedRouteRepository is used to save the calculated routes
    private final CombinedRouteRepository combinedRouteRepository;

    // The constructor
    @Autowired
    public CombinedRouteService(FlightRouteRepository flightRouteRepository,
                                CombinedRouteRepository combinedRouteRepository) {
        this.flightRouteRepository = flightRouteRepository;
        this.combinedRouteRepository = combinedRouteRepository;
    }

    // Generates and persists all combinations of routes
    public void generateAndSaveRouteCombinations() {
        // Check if there are existing combined routes in the database
        if (combinedRouteRepository.count() > 0) {
            // If there are, delete them before generating new combinations
            combinedRouteRepository.deleteAll();
            System.out.println("Existing combined routes deleted.");
        }

        List<FlightRoute> allRoutes = flightRouteRepository.findAll(); // Get all possible routes

        for (FlightRoute initialRoute : allRoutes) {
            List<CombinedRoute> combinations = generateCombinations(initialRoute, allRoutes);
            combinedRouteRepository.saveAll(combinations);
        }

        System.out.println("Combined routes saved successfully.");
    }

    // Recursive method to generate all combinations starting from a specific route
    private List<CombinedRoute> generateCombinations(FlightRoute startRoute, List<FlightRoute> allRoutes) {
        List<CombinedRoute> results = new ArrayList<>();
        generateCombinationsRecursive(startRoute, allRoutes, new ArrayList<>(), results, new HashSet<>());
        return results;
    }

    // Recursive helper to build combinations of routes, avoiding visited locations to prevent loops
    private void generateCombinationsRecursive(FlightRoute currentRoute, List<FlightRoute> allRoutes,
                                               List<FlightRoute> currentCombination, List<CombinedRoute> results,
                                               Set<String> visitedLocations) {
        // Check if the current location has been visited before to avoid loops
        // base case 1 for recursion
        if (!visitedLocations.add(currentRoute.getFromName())) {
            return; // Prevents cycles by stopping recursion if the location has been visited
        }

        currentCombination.add(currentRoute);  // Adds the current route to the ongoing route combination

        // Find feasible next routes based on current route's destination and timing constraints
        List<FlightRoute> nextRoutes = allRoutes.stream()
                .filter(route -> route.getFromName().equals(currentRoute.getToName())) // Prevent revisiting locations
                .filter(route -> !route.getFlightStart().isBefore(currentRoute.getFlightEnd())) // Ensure the next flight does not start before the current one ends
                .collect(Collectors.toList());

        // base case 2 for recursion
        if (nextRoutes.isEmpty()) {
            // If no further extensions are possible, finalize this combination
            results.add(createCombinedRoute(currentCombination));
        } else {
            // Continue to extend the current route combination recursively
            for (FlightRoute nextRoute : nextRoutes) {
                generateCombinationsRecursive(nextRoute, allRoutes, new ArrayList<>(currentCombination), results, new HashSet<>(visitedLocations));
            }
        }
    }

    // Constructs a CombinedRoute object from a list of FlightRoute
    private CombinedRoute createCombinedRoute(List<FlightRoute> flightRoutes) {
        UUID combinedRouteId = UUID.randomUUID();
        String fromName = flightRoutes.get(0).getFromName();
        String toName = flightRoutes.get(flightRoutes.size() - 1).getToName();

        // Builds a string representation of the complete route
        String route = flightRoutes.stream()
                .map(FlightRoute::getFromName)
                .distinct()
                .collect(Collectors.joining(", "))
                + ", " + toName;

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

        return CombinedRoute.builder() // Utilizes the Builder pattern for creating a CombinedRoute instance
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

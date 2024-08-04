package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.Leg;
import com.bocklercode.cosmos_odyssey_core.model.Provider;
import com.bocklercode.cosmos_odyssey_core.model.Route;
import com.bocklercode.cosmos_odyssey_core.repository.LegRepository;
import com.bocklercode.cosmos_odyssey_core.repository.ProviderRepository;
import com.bocklercode.cosmos_odyssey_core.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@DependsOn("dataLoaderService")
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    private final LegRepository legRepository;
    private final ProviderRepository providerRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public RouteService(LegRepository legRepository, ProviderRepository providerRepository, RouteRepository routeRepository) {
        this.legRepository = legRepository;
        this.providerRepository = providerRepository;
        this.routeRepository = routeRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing RouteService...");
        if (isRouteDataLoaded()) {
            logger.info("Route data is already loaded, skipping initialization.");
        } else {
            logger.info("Route data not found, calculating and saving routes.");
            calculateAndSaveRoutes();
        }
    }

    private boolean isRouteDataLoaded() {
        long count = routeRepository.count();
        logger.info("Checking if route data is loaded. Count: {}", count);
        return count > 0;
    }

    public void calculateAndSaveRoutes() {
        logger.info("Fetching all legs from the repository.");
        List<Leg> allLegs = legRepository.findAll();
        logger.info("Total legs fetched: {}", allLegs.size());

        Map<UUID, List<Leg>> graph = new HashMap<>();

        logger.info("Building graph from legs.");
        for (Leg leg : allLegs) {
            graph.computeIfAbsent(leg.getFromId(), k -> new ArrayList<>()).add(leg);
        }
        logger.info("Graph built with nodes: {}", graph.keySet());

        for (UUID startId : graph.keySet()) {
            logger.info("Processing routes starting from node: {}", startId);
            for (Leg leg : graph.get(startId)) {
                Set<UUID> visited = new HashSet<>();
                visited.add(startId);
                List<String> path = new ArrayList<>();
                path.add(leg.getFromName());
                List<String> pathIds = new ArrayList<>();
                pathIds.add(leg.getFromId().toString());
                Set<String> providers = new HashSet<>();
                logger.info("Starting recursive search from leg: {}", leg);
                findAndSaveFirstRouteRecursive(graph, leg, leg.getDistance(), getLowestPrice(leg), visited, startId, path, pathIds, providers, 0L);
            }
        }
    }

    private boolean findAndSaveFirstRouteRecursive(Map<UUID, List<Leg>> graph, Leg currentLeg, long currentDistance, BigDecimal currentPrice,
                                                   Set<UUID> visited, UUID startId, List<String> path, List<String> pathIds, Set<String> providers, long currentDuration) {
        UUID currentId = currentLeg.getToId();
        path.add(currentLeg.getToName());
        pathIds.add(currentLeg.getToId().toString());
        List<Provider> legProviders = getProvidersForLeg(currentLeg);
        if (legProviders.isEmpty()) {
            logger.warn("No providers found for leg: {}", currentLeg.getLegId());
        } else {
            logger.info("Providers found for leg: {}", legProviders);
        }
        providers.addAll(getProviderNames(legProviders));
        long legDuration = legProviders.stream().mapToLong(Provider::getDuration).sum();
        currentDuration += legDuration;

        logger.info("Current path: {}", path);
        logger.info("Current path IDs: {}", pathIds);
        logger.info("Current providers: {}", providers);
        logger.info("Current distance: {}", currentDistance);
        logger.info("Current price: {}", currentPrice);
        logger.info("Current duration: {}", currentDuration);

        for (Leg neighbor : graph.getOrDefault(currentId, Collections.emptyList())) {
            if (!visited.contains(neighbor.getToId())) {
                visited.add(neighbor.getToId());
                long newDistance = currentDistance + neighbor.getDistance();
                BigDecimal newPrice = currentPrice.add(getLowestPrice(neighbor));

                logger.info("Creating route: startId={}, endId={}, newDistance={}, newPrice={}, currentDuration={}",
                        startId, neighbor.getToId(), newDistance, newPrice, currentDuration);

                Route route = Route.builder()
                        .startId(startId)
                        .endId(neighbor.getToId())
                        .path(String.join(" -> ", path))
                        .totalDistance(BigDecimal.valueOf(newDistance))
                        .totalPrice(newPrice)
                        .totalDuration(currentDuration)
                        .build();

                logger.debug("Attempting to save route: {}", route);

                try {
                    routeRepository.save(route);
                    logger.info("Route saved: {}", route);
                    return true; // stop recursion after saving the first route
                } catch (Exception e) {
                    logger.error("Error saving route: {}", route, e);
                }

                if (findAndSaveFirstRouteRecursive(graph, neighbor, newDistance, newPrice, visited, startId, path, pathIds, providers, currentDuration)) {
                    return true;
                }
                visited.remove(neighbor.getToId());
            }
        }

        // Eemaldame viimase elemendi, kui läheme tagasi rekursioonist
        path.removeLast();
        pathIds.removeLast();
        currentDuration -= legDuration;
        logger.info("Backtracking, updated path: {}", path);
        logger.info("Backtracking, updated path IDs: {}", pathIds);
        logger.info("Backtracking, updated duration: {}", currentDuration);

        return false; // continue recursion if no route was saved
    }

    private BigDecimal getLowestPrice(Leg leg) {
        logger.info("Fetching lowest price for leg: {}", leg.getLegId());
        List<Provider> providers = providerRepository.findAllByLeg_LegId(leg.getLegId());
        if (providers.isEmpty()) {
            logger.warn("No providers found for leg: {}", leg.getLegId());
        }
        BigDecimal lowestPrice = providers.stream()
                .map(Provider::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        logger.debug("Lowest price for leg {} is {}", leg.getLegId(), lowestPrice);
        return lowestPrice;
    }

    private List<Provider> getProvidersForLeg(Leg leg) {
        logger.info("Fetching providers for leg: {}", leg.getLegId());
        return providerRepository.findAllByLeg_LegId(leg.getLegId());
    }

    private Set<String> getProviderNames(List<Provider> providers) {
        Set<String> providerNames = new HashSet<>();
        for (Provider provider : providers) {
            providerNames.add(provider.getCompanyName());
        }
        return providerNames;
    }

    public List<Route> findRoutes(UUID fromId, UUID toId) {
        logger.info("Fetching routes from {} to {}", fromId, toId);
        return routeRepository.findAllByStartIdAndEndId(fromId, toId);
    }
}

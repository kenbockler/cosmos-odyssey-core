package com.bocklercode.cosmos_odyssey_core.service.impl;

import com.bocklercode.cosmos_odyssey_core.model.Route;
import com.bocklercode.cosmos_odyssey_core.model.TravelRoute;
import com.bocklercode.cosmos_odyssey_core.repository.RouteRepository;
import com.bocklercode.cosmos_odyssey_core.repository.TravelRouteRepository;
import com.bocklercode.cosmos_odyssey_core.service.TravelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TravelRouteServiceImpl implements TravelRouteService {

    private final TravelRouteRepository travelRouteRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public TravelRouteServiceImpl(TravelRouteRepository travelRouteRepository, RouteRepository routeRepository) {
        this.travelRouteRepository = travelRouteRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public void calculateAndSaveTravelRoutes() {
        List<Route> routes = routeRepository.findAll();
        Map<UUID, List<Route>> graph = new HashMap<>();

        // Ehitame graafi
        for (Route route : routes) {
            graph.putIfAbsent(route.getFromId(), new ArrayList<>());
            graph.get(route.getFromId()).add(route);
        }

        // Arvutame kõik võimalikud teekonnad ja salvestame need
        List<TravelRoute> travelRoutes = new ArrayList<>();
        for (Route route : routes) {
            findAllPaths(route.getFromId(), route.getToId(), graph, new ArrayList<>(), travelRoutes);
        }

        travelRouteRepository.saveAll(travelRoutes);
    }

    private void findAllPaths(UUID fromId, UUID toId, Map<UUID, List<Route>> graph, List<Route> path, List<TravelRoute> travelRoutes) {
        if (fromId.equals(toId)) {
            // Kui jõudsime sihtkohta, salvestame teekonna
            long totalTime = path.stream().mapToLong(Route::getDistance).sum();
            BigDecimal totalPrice = path.stream().map(Route::getDistance).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
            long totalDuration = path.stream().mapToLong(Route::getDistance).sum();

            TravelRoute travelRoute = TravelRoute.builder()
                    .travelRouteId(UUID.randomUUID())
                    .fromId(path.get(0).getFromId())
                    .fromName(path.get(0).getFromName())
                    .toId(path.get(path.size() - 1).getToId())
                    .toName(path.get(path.size() - 1).getToName())
                    .totalQuotedTravelTime(totalTime)
                    .totalQuotedPrice(totalPrice)
                    .totalQuotedDuration(totalDuration)
                    .build();

            travelRoutes.add(travelRoute);
            return;
        }

        List<Route> nextRoutes = graph.getOrDefault(fromId, new ArrayList<>());
        for (Route nextRoute : nextRoutes) {
            if (!path.contains(nextRoute)) {
                path.add(nextRoute);
                findAllPaths(nextRoute.getToId(), toId, graph, path, travelRoutes);
                path.remove(nextRoute);
            }
        }
    }

    @Override
    public List<TravelRoute> findRoutes(UUID fromId, UUID toId) {
        // Siin võib olla spetsiaalne loogika, mis leiab marsruudid andmebaasist
        return travelRouteRepository.findAll().stream()
                .filter(route -> route.getFromId().equals(fromId) && route.getToId().equals(toId))
                .collect(Collectors.toList());
    }
}

package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.*;
import com.bocklercode.cosmos_odyssey_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DataLoaderService implements ApplicationRunner {

    private final APIClient apiClient;
    private final LegRepository legRepository;
    private final PriceListRepository priceListRepository;
    private final ProviderRepository providerRepository;
    private final RouteRepository routeRepository;
    private final CalculatedRouteRepository calculatedRouteRepository;
    private final ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository;
    private final GraphService graphService;
    private final CalculatedRouteService calculatedRouteService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataLoaderService(APIClient apiClient,
                             LegRepository legRepository,
                             PriceListRepository priceListRepository,
                             ProviderRepository providerRepository,
                             RouteRepository routeRepository,
                             CalculatedRouteRepository calculatedRouteRepository,
                             ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository,
                             GraphService graphService,
                             CalculatedRouteService calculatedRouteService,
                             JdbcTemplate jdbcTemplate) {
        this.apiClient = apiClient;
        this.legRepository = legRepository;
        this.priceListRepository = priceListRepository;
        this.providerRepository = providerRepository;
        this.routeRepository = routeRepository;
        this.calculatedRouteRepository = calculatedRouteRepository;
        this.providerLegRouteCombinedRepository = providerLegRouteCombinedRepository;
        this.graphService = graphService;
        this.calculatedRouteService = calculatedRouteService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isDataLoaded()) {
            loadData();
        } else {
            System.out.println("Data is already loaded, skipping data loading.");
        }

        if (!isCombinedTableDataLoaded()) {
            createCombinedTable();
        } else {
            System.out.println("Combined table data is already loaded, skipping table creation.");
        }

        if (!isCalculatedRoutesDataLoaded()) {
            processCalculatedRoutes();
        } else {
            System.out.println("Calculated routes are already loaded, skipping graph building.");
        }
    }

    private boolean isDataLoaded() {
        return priceListRepository.count() > 0;
    }

    private boolean isCombinedTableDataLoaded() {
        return providerLegRouteCombinedRepository.count() > 0;
    }

    private boolean isCalculatedRoutesDataLoaded() {
        return calculatedRouteRepository.count() > 0;
    }

    public void loadData() {
        APIResponse apiResponse = apiClient.getTravelPrices();

        UUID priceListId = UUID.fromString(apiResponse.getId());
        Instant validUntil = Instant.parse(apiResponse.getValidUntil());

        // Salvesta price list
        PriceList priceList = PriceList.builder()
                .priceListId(priceListId)
                .validUntil(validUntil)
                .build();
        priceListRepository.save(priceList);

        // Salvesta legs andmed
        for (APIResponse.Leg apiLeg : apiResponse.getLegs()) {
            Leg leg = Leg.builder()
                    .legId(UUID.randomUUID())
                    .priceList(priceList)
                    .build();
            legRepository.save(leg);

            // Salvesta providers andmed
            for (APIResponse.Leg.Provider apiProvider : apiLeg.getProviders()) {
                Instant flightStart = Instant.parse(apiProvider.getFlightStart());
                Instant flightEnd = Instant.parse(apiProvider.getFlightEnd());

                Provider provider = Provider.builder()
                        .providerId(UUID.randomUUID())
                        .companyId(UUID.fromString(apiProvider.getCompany().getId()))
                        .companyName(apiProvider.getCompany().getName())
                        .price(BigDecimal.valueOf(apiProvider.getPrice()))
                        .flightStart(flightStart)
                        .flightEnd(flightEnd)
                        .leg(leg)
                        .build();
                providerRepository.save(provider);

                // Arvuta kestvus ja uuenda provider
                long duration = Duration.between(flightStart, flightEnd).toMillis();
                provider.setDuration(duration);
                providerRepository.save(provider);
            }

            // Salvesta route andmed
            Route route = Route.builder()
                    .routeId(UUID.randomUUID())
                    .fromId(UUID.fromString(apiLeg.getRouteInfo().getFrom().getId()))
                    .fromName(apiLeg.getRouteInfo().getFrom().getName())
                    .toId(UUID.fromString(apiLeg.getRouteInfo().getTo().getId()))
                    .toName(apiLeg.getRouteInfo().getTo().getName())
                    .distance(apiLeg.getRouteInfo().getDistance())
                    .leg(leg)
                    .build();
            routeRepository.save(route);
        }
    }

    private void createCombinedTable() {
        String dropSql = "DROP TABLE IF EXISTS provider_leg_route_combined";
        jdbcTemplate.execute(dropSql);

        String createSql = "CREATE TABLE provider_leg_route_combined AS " +
                "SELECT p.provider_id, p.company_id, p.company_name, p.price, p.flight_start, p.flight_end, p.duration, " +
                "l.leg_id, r.from_id, r.from_name, r.to_id, r.to_name, r.distance " +
                "FROM providers p " +
                "JOIN legs l ON p.leg_id = l.leg_id " +
                "JOIN routes r ON l.leg_id = r.leg_id";
        jdbcTemplate.execute(createSql);

        System.out.println("Combined table created successfully.");
    }

    private void processCalculatedRoutes() {
        // Ehita ja prindi graaf
        Map<String, List<GraphEdge>> graph = graphService.buildGraph();

        // Otsime ja salvestame lühimad teed (näiteks Earth'ist kõikidele teistele)
        for (String planet : graph.keySet()) {
            if (!planet.equals("Earth")) {
                calculatedRouteService.findAndSaveShortestPath(graph, "Earth", planet);
            }
        }
    }
}

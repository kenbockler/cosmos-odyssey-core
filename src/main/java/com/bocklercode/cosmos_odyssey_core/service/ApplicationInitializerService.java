package com.bocklercode.cosmos_odyssey_core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Service to handle initial application data setup including creating combined data tables
@Service
public class ApplicationInitializerService {

    private final JdbcTemplate jdbcTemplate;
    private final FlightRouteService flightRouteService;
    private final CombinedRouteService combinedRouteService;
    private final APIDataLoaderService apiDataLoaderService;

    @Autowired
    public ApplicationInitializerService(JdbcTemplate jdbcTemplate,
                                         FlightRouteService flightRouteService,
                                         CombinedRouteService combinedRouteService,
                                         APIDataLoaderService apiDataLoaderService) {
        this.jdbcTemplate = jdbcTemplate;
        this.flightRouteService = flightRouteService;
        this.combinedRouteService = combinedRouteService;
        this.apiDataLoaderService = apiDataLoaderService;
    }

    // Initializes data by loading new data and setting up combined tables if necessary
    public boolean initializeData() {
        boolean dataUpdated = apiDataLoaderService.loadData();
        if (dataUpdated) {
            createCombinedTable();
            flightRouteService.saveFlightRoutes();
            combinedRouteService.generateAndSaveRouteCombinations();
            return true;
        } else {
            System.out.println("API data is already loaded, skipping data loading.");
            return false;
        }
    }

    // Creates a combined table for current active price list details
    public void createCombinedTable() {
        String activePriceListIdSql = "SELECT price_list_id FROM price_lists WHERE is_active = true";
        UUID activePriceListId = jdbcTemplate.queryForObject(activePriceListIdSql, UUID.class);

        String dropSql = "DROP TABLE IF EXISTS provider_leg_route_combined";
        jdbcTemplate.execute(dropSql);

        String createSql = "CREATE TABLE provider_leg_route_combined AS " +
                "SELECT p.provider_id, p.company_id, p.company_name, p.price, p.flight_start, p.flight_end, p.duration, " +
                "l.leg_id, r.from_id, r.from_name, r.to_id, r.to_name, r.distance " +
                "FROM providers p " +
                "JOIN legs l ON p.leg_id = l.leg_id " +
                "JOIN routes r ON l.leg_id = r.leg_id " +
                "WHERE l.price_list_id = ?";
        jdbcTemplate.update(createSql, activePriceListId);

        System.out.println("Combined table created successfully for active price list.");
    }
}

package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.*;
import com.bocklercode.cosmos_odyssey_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class DataLoaderService {

    private final APIClient apiClient;
    private final LegRepository legRepository;
    private final PriceListRepository priceListRepository;
    private final ProviderRepository providerRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataLoaderService(APIClient apiClient,
                             LegRepository legRepository,
                             PriceListRepository priceListRepository,
                             ProviderRepository providerRepository,
                             JdbcTemplate jdbcTemplate) {
        this.apiClient = apiClient;
        this.legRepository = legRepository;
        this.priceListRepository = priceListRepository;
        this.providerRepository = providerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        if (isDataLoaded()) {
            System.out.println("Data is already loaded, skipping initialization.");
        } else {
            loadData();
            createCombinedTable();
        }
    }

    public void loadData() {
        APIResponse apiResponse = apiClient.getTravelPrices();

        UUID pricelistId = UUID.fromString(apiResponse.getId());
        Instant validUntil = Instant.parse(apiResponse.getValidUntil());

        // Salvesta price list
        PriceList priceList = PriceList.builder()
                .id(pricelistId)
                .validUntil(validUntil)
                .createdAt(Instant.now())
                .build();
        priceListRepository.save(priceList);

        // Salvesta legs andmed
        for (APIResponse.Leg apiLeg : apiResponse.getLegs()) {
            Leg leg = new Leg();
            leg.setLegId(UUID.randomUUID());
            leg.setRouteId(UUID.fromString(apiLeg.getRouteInfo().getId()));
            leg.setFromId(UUID.fromString(apiLeg.getRouteInfo().getFrom().getId()));
            leg.setFromName(apiLeg.getRouteInfo().getFrom().getName());
            leg.setToId(UUID.fromString(apiLeg.getRouteInfo().getTo().getId()));
            leg.setToName(apiLeg.getRouteInfo().getTo().getName());
            leg.setDistance(apiLeg.getRouteInfo().getDistance());
            leg.setPricelist(priceList);
            legRepository.save(leg);

            // Salvesta providers andmed
            for (APIResponse.Leg.Provider apiProvider : apiLeg.getProviders()) {
                Provider provider = new Provider();
                provider.setId(UUID.randomUUID());
                provider.setCompanyId(UUID.fromString(apiProvider.getCompany().getId()));
                provider.setCompanyName(apiProvider.getCompany().getName());
                provider.setPrice(BigDecimal.valueOf(apiProvider.getPrice()));
                provider.setFlightStart(Instant.parse(apiProvider.getFlightStart()));
                provider.setFlightEnd(Instant.parse(apiProvider.getFlightEnd()));
                provider.setLeg(leg);
                providerRepository.save(provider);
            }
        }
    }

    private void createCombinedTable() {
        String sql = "CREATE TABLE IF NOT EXISTS provider_leg_pricelist_combined AS SELECT p.provider_id, p.company_id, p.company_name, p.price, p.flight_start, p.flight_end, p.duration, l.leg_id, l.route_id, l.from_id, l.from_name, l.to_id, l.to_name, l.distance, pr.pricelist_id, pr.valid_until, pr.created_at FROM providers p JOIN legs l ON p.leg_id = l.leg_id JOIN pricelists pr ON l.pricelist_id = pr.pricelist_id;";
        jdbcTemplate.execute(sql);
        System.out.println("Combined table created successfully.");
    }

    private boolean isDataLoaded() {
        return priceListRepository.count() > 0;
    }
}

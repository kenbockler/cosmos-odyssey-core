package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.*;
import com.bocklercode.cosmos_odyssey_core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class DataLoaderService {

    private final APIClient apiClient;
    private final LegsRepository legsRepository;
    private final PriceListRepository priceListRepository;
    private final ProvidersRepository providersRepository;

    @Autowired
    public DataLoaderService(APIClient apiClient,
                             LegsRepository legsRepository,
                             PriceListRepository priceListRepository,
                             ProvidersRepository providersRepository) {
        this.apiClient = apiClient;
        this.legsRepository = legsRepository;
        this.priceListRepository = priceListRepository;
        this.providersRepository = providersRepository;
    }

    @PostConstruct
    public void init() {
        if (isDataLoaded()) {
            System.out.println("Data is already loaded, skipping initialization.");
        } else {
            loadData();
        }
    }

    public void loadData() {
        APIResponse apiResponse = apiClient.getTravelPrices();

        UUID pricelistId = UUID.fromString(apiResponse.getId());
        Instant validUntil = Instant.parse(apiResponse.getValidUntil());

        // Salvesta legs andmed
        for (APIResponse.Leg apiLeg : apiResponse.getLegs()) {
            Leg leg = new Leg();
            leg.setLegsId(UUID.randomUUID());
            leg.setRouteId(UUID.fromString(apiLeg.getRouteInfo().getId()));
            leg.setFromId(UUID.fromString(apiLeg.getRouteInfo().getFrom().getId()));
            leg.setFromName(apiLeg.getRouteInfo().getFrom().getName());
            leg.setToId(UUID.fromString(apiLeg.getRouteInfo().getTo().getId()));
            leg.setToName(apiLeg.getRouteInfo().getTo().getName());
            leg.setDistance(apiLeg.getRouteInfo().getDistance());
            legsRepository.save(leg);

            // Salvesta providers andmed
            for (APIResponse.Leg.Provider apiProvider : apiLeg.getProviders()) {
                Provider provider = new Provider();
                provider.setId(UUID.randomUUID());
                provider.setCompanyId(UUID.fromString(apiProvider.getCompany().getId()));
                provider.setCompanyName(apiProvider.getCompany().getName());
                provider.setPrice(BigDecimal.valueOf(apiProvider.getPrice()));
                provider.setFlightStart(Instant.parse(apiProvider.getFlightStart()));
                provider.setFlightEnd(Instant.parse(apiProvider.getFlightEnd()));
                provider.setLegs(leg);
                providersRepository.save(provider);
            }

            // Salvesta price list
            PriceList priceList = PriceList.builder()
                    .id(pricelistId)
                    .validUntil(validUntil)
                    .createdAt(Instant.now())
                    .leg(leg)
                    .build();
            priceListRepository.save(priceList);
        }
    }

    private boolean isDataLoaded() {
        return priceListRepository.count() > 0;
    }
}

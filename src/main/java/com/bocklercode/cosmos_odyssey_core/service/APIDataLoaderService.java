package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.*;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.LegRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.PriceListRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.ProviderRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Service for loading travel price data from an external API and updating the database accordingly
@Service
public class APIDataLoaderService {

    private final APIClient apiClient;
    private final LegRepository legRepository;
    private final PriceListRepository priceListRepository;
    private final ProviderRepository providerRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public APIDataLoaderService(APIClient apiClient,
                                LegRepository legRepository,
                                PriceListRepository priceListRepository,
                                ProviderRepository providerRepository,
                                RouteRepository routeRepository) {
        this.apiClient = apiClient;
        this.legRepository = legRepository;
        this.priceListRepository = priceListRepository;
        this.providerRepository = providerRepository;
        this.routeRepository = routeRepository;
    }

    // Transactional method to ensure all database operations complete successfully as a single transaction
    @Transactional
    public boolean loadData() {
        APIResponse apiResponse = apiClient.getTravelPrices();

        UUID newPriceListId = UUID.fromString(apiResponse.getId());
        Instant newValidUntil = Instant.parse(apiResponse.getValidUntil());

        Optional<PriceList> activePriceListOpt = priceListRepository.findActivePriceList();

        if (activePriceListOpt.isPresent()) {
            PriceList activePriceList = activePriceListOpt.get();

            // Check if there is a newer price list available and update if necessary
            if (!newPriceListId.equals(activePriceList.getPriceListId()) &&
                    newValidUntil.isAfter(activePriceList.getValidUntil())) {

                // Deactivate all previous price lists and save the new one
                priceListRepository.deactivateAllPriceLists();

                PriceList priceList = PriceList.builder()
                        .priceListId(newPriceListId)
                        .validUntil(newValidUntil)
                        .isActive(true)
                        .build();
                priceListRepository.save(priceList);

                // Save new legs, providers, and routes from API response
                saveLegsAndProviders(apiResponse, priceList);

                // Cleanup older price lists to maintain a limited number of records
                cleanupOldPriceLists();

                // Return true as new data has been successfully saved
                return true;
            } else {
                System.out.println("Hinnakiri on juba uusim ja kehtiv. Ei laadita uusi andmeid.");
                return false;
            }
        } else {
            // If no active price list is found, create and save a new one
            PriceList priceList = PriceList.builder()
                    .priceListId(newPriceListId)
                    .validUntil(newValidUntil)
                    .isActive(true)
                    .build();
            priceListRepository.save(priceList);

            saveLegsAndProviders(apiResponse, priceList);

            // Cleanup older price lists to maintain a limited number of records
            cleanupOldPriceLists();

            // Return true as new data has been successfully saved
            return true;
        }
    }

    // Saves new legs and providers based on the API response
    private void saveLegsAndProviders(APIResponse apiResponse, PriceList priceList) {
        for (APIResponse.Leg apiLeg : apiResponse.getLegs()) {
            Leg leg = Leg.builder()
                    .legId(UUID.randomUUID())
                    .priceList(priceList)
                    .build();
            legRepository.save(leg);

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

                long duration = Duration.between(flightStart, flightEnd).toMillis();
                provider.setDuration(duration);
                providerRepository.save(provider);
            }

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

    // Deletes older price lists
    private void cleanupOldPriceLists() {
        List<PriceList> allPriceLists = priceListRepository.findAllOrderByValidUntilAsc();
        int excessCount = allPriceLists.size() - 15;

        if (excessCount > 0) {
            List<PriceList> toDelete = allPriceLists.subList(0, excessCount);
            for (PriceList priceList : toDelete) {
                UUID priceListId = priceList.getPriceListId();

                // Delete related data from 'providers' table
                providerRepository.deleteByPriceListId(priceListId);

                // Delete related data from 'routes' table
                routeRepository.deleteByPriceListId(priceListId);

                // Delete related data from 'legs' table
                legRepository.deleteByPriceListId(priceListId);

                // Finally, delete the price list itself
                priceListRepository.delete(priceList);
            }
        }
    }
}

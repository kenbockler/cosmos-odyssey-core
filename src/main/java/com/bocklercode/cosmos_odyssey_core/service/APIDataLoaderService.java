package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.*;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.LegRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.PriceListRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.ProviderRepository;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.RouteRepository;
import com.bocklercode.cosmos_odyssey_core.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class APIDataLoaderService {

    private final APIClient apiClient;
    private final LegRepository legRepository;
    private final PriceListRepository priceListRepository;
    private final ProviderRepository providerRepository;
    private final RouteRepository routeRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public APIDataLoaderService(APIClient apiClient,
                                LegRepository legRepository,
                                PriceListRepository priceListRepository,
                                ProviderRepository providerRepository,
                                RouteRepository routeRepository,
                                ReservationRepository reservationRepository) {
        this.apiClient = apiClient;
        this.legRepository = legRepository;
        this.priceListRepository = priceListRepository;
        this.providerRepository = providerRepository;
        this.routeRepository = routeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public boolean loadData() {
        APIResponse apiResponse = apiClient.getTravelPrices();

        UUID newPriceListId = UUID.fromString(apiResponse.getId());
        Instant newValidUntil = Instant.parse(apiResponse.getValidUntil());

        Optional<PriceList> activePriceListOpt = priceListRepository.findActivePriceList();

        if (activePriceListOpt.isPresent()) {
            PriceList activePriceList = activePriceListOpt.get();

            if (!newPriceListId.equals(activePriceList.getPriceListId()) &&
                    newValidUntil.isAfter(activePriceList.getValidUntil())) {

                priceListRepository.deactivateAllPriceLists();

                PriceList priceList = PriceList.builder()
                        .priceListId(newPriceListId)
                        .validUntil(newValidUntil)
                        .isActive(true)
                        .build();
                priceListRepository.save(priceList);

                saveLegsAndProviders(apiResponse, priceList);

                cleanupOldPriceLists();

                return true;
            } else {
                System.out.println("The price list is already up-to-date and valid. No new data will be loaded.");
                return false;
            }
        } else {
            PriceList priceList = PriceList.builder()
                    .priceListId(newPriceListId)
                    .validUntil(newValidUntil)
                    .isActive(true)
                    .build();
            priceListRepository.save(priceList);

            saveLegsAndProviders(apiResponse, priceList);

            cleanupOldPriceLists();

            return true;
        }
    }

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

    private void cleanupOldPriceLists() {
        List<PriceList> allPriceLists = priceListRepository.findAllOrderByValidUntilAsc();
        int excessCount = allPriceLists.size() - 15;

        if (excessCount > 0) {
            List<PriceList> toDelete = allPriceLists.subList(0, excessCount);
            for (PriceList priceList : toDelete) {
                UUID priceListId = priceList.getPriceListId();

                // Kustutame seotud reserveeringud
                reservationRepository.deleteByPriceListId(priceListId);

                // Kustutame seotud andmed 'providers' tabelist
                providerRepository.deleteByPriceListId(priceListId);

                // Kustutame seotud andmed 'routes' tabelist
                routeRepository.deleteByPriceListId(priceListId);

                // Kustutame seotud andmed 'legs' tabelist
                legRepository.deleteByPriceListId(priceListId);

                // Kustutame hinnakiri ise
                priceListRepository.delete(priceList);
            }
        }
    }
}

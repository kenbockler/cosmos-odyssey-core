package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.PriceList;
import com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Class to handle scheduled loading and updating data
@Service
public class ScheduledDataLoaderService {

    private final PriceListRepository priceListRepository;
    private final ApplicationInitializerService applicationInitializerService;
    // ScheduledExecutorService to manage scheduled tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public ScheduledDataLoaderService(PriceListRepository priceListRepository,
                                      ApplicationInitializerService applicationInitializerService) {
        this.priceListRepository = priceListRepository;
        this.applicationInitializerService = applicationInitializerService;

        scheduleNextUpdate(); // Schedule the next update on application startup
    }

    // Schedules the next data update based on the validity of the current price list
    private void scheduleNextUpdate() {
        Optional<PriceList> activePriceListOpt = priceListRepository.findActivePriceList();

        if (activePriceListOpt.isPresent()) {
            PriceList activePriceList = activePriceListOpt.get();
            Instant validUntil = activePriceList.getValidUntil();
            long delay = Duration.between(Instant.now(), validUntil).toMillis();

            System.out.println("Current time: " + Instant.now());
            System.out.println("Next API check scheduled for: " + validUntil);

            // If the delay is too small or negative, handle it to prevent immediate rescheduling
            if (delay > 1000) {  // Only schedule if the delay is more than 1 second
                scheduler.schedule(this::updateData, delay, TimeUnit.MILLISECONDS);
            } else {
                System.out.println("ValidUntil time has passed or is too close. Triggering immediate update.");
                updateData();
            }
        } else {
            System.out.println("No active price list found. Initiating data load immediately.");
            updateData(); // Load data immediately if no active price list is found
        }
    }

    // Updates the data and schedules the next update
    public void updateData() {
        try {
            boolean dataUpdated = applicationInitializerService.initializeData();
            if (dataUpdated) {
                System.out.println("Data successfully updated. Scheduling next update.");
                scheduleNextUpdate(); // Reschedule the next update based on new `validUntil` time
            } else {
                System.out.println("Data is already up-to-date. No further updates needed.");
            }
        } catch (Exception e) {
            System.out.println("Failed to update data. Retrying in 10 seconds.");
            scheduler.schedule(this::updateData, 10, TimeUnit.SECONDS);  // Retry in 10 seconds if update fails
        }
    }
}

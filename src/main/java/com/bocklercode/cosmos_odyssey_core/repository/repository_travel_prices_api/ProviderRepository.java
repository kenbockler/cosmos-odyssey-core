package com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    @Modifying
    @Query("DELETE FROM Provider p WHERE p.leg.priceList.priceListId = :priceListId")
    void deleteByPriceListId(UUID priceListId);

}
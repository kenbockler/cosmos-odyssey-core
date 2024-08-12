package com.bocklercode.cosmos_odyssey_core.repository.repository_travel_prices_api;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE PriceList p SET p.isActive = false WHERE p.isActive = true")
    void deactivateAllPriceLists();

    @Query("SELECT p FROM PriceList p WHERE p.isActive = true")
    Optional<PriceList> findActivePriceList();

    @Query("SELECT p FROM PriceList p ORDER BY p.validUntil ASC")
    List<PriceList> findAllOrderByValidUntilAsc();

    @Query("SELECT p FROM PriceList p WHERE p.priceListId = :priceListId")
    Optional<PriceList> findByPriceListId(UUID priceListId);
}

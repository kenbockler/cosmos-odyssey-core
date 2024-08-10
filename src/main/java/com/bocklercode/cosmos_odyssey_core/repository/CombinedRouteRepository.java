package com.bocklercode.cosmos_odyssey_core.repository;

import com.bocklercode.cosmos_odyssey_core.model.CombinedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CombinedRouteRepository extends JpaRepository<CombinedRoute, UUID> {
    List<CombinedRoute> findByFromNameAndToName(String fromName, String toName);
}

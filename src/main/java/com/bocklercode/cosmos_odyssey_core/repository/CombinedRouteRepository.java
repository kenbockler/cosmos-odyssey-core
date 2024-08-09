package com.bocklercode.cosmos_odyssey_core.repository;

import com.bocklercode.cosmos_odyssey_core.model.CombinedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CombinedRouteRepository extends JpaRepository<CombinedRoute, UUID> {

    @Query("SELECT cr FROM CombinedRoute cr WHERE cr.fromName = :fromName AND cr.toName = :toName")
    List<CombinedRoute> findRoutesByFromAndTo(@Param("fromName") String fromName, @Param("toName") String toName);
}

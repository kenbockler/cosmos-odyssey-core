package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.model.RouteCombination;
import com.bocklercode.cosmos_odyssey_core.repository.RouteCombinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CombinedRouteController {

    private final RouteCombinationRepository routeCombinationRepository;

    @Autowired
    public CombinedRouteController(RouteCombinationRepository routeCombinationRepository) {
        this.routeCombinationRepository = routeCombinationRepository;
    }

    @GetMapping("/routes")
    public List<RouteCombination> getRoutes(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName) {
        return routeCombinationRepository.findRoutesByFromAndTo(fromName, toName);
    }
}

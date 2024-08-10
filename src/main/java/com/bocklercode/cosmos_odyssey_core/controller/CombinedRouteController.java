package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.model.CombinedRoute;
import com.bocklercode.cosmos_odyssey_core.repository.CombinedRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CombinedRouteController {

    private final CombinedRouteRepository combinedRouteRepository;

    @Autowired
    public CombinedRouteController(CombinedRouteRepository combinedRouteRepository) {
        this.combinedRouteRepository = combinedRouteRepository;
    }

    @GetMapping("/routes")
    public List<CombinedRoute> getRoutes(
            @RequestParam("from") String fromName,
            @RequestParam("to") String toName) {
        return combinedRouteRepository.findByFromNameAndToName(fromName, toName);
    }
}
package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.model.TravelRoute;
import com.bocklercode.cosmos_odyssey_core.service.TravelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/travelRoutes")
public class TravelRouteController {

    private final TravelRouteService travelRouteService;

    @Autowired
    public TravelRouteController(TravelRouteService travelRouteService) {
        this.travelRouteService = travelRouteService;
    }

    @GetMapping("/calculate")
    public String calculateTravelRoutes() {
        travelRouteService.calculateAndSaveTravelRoutes();
        return "Travel routes calculated and saved successfully";
    }

    @GetMapping("/find")
    public List<TravelRoute> findTravelRoutes(@RequestParam UUID fromId, @RequestParam UUID toId) {
        return travelRouteService.findRoutes(fromId, toId);
    }
}

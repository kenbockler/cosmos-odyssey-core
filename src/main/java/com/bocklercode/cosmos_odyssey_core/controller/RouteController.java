package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.model.Route;
import com.bocklercode.cosmos_odyssey_core.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/calculate")
    public String calculateRoutes() {
        routeService.calculateAndSaveRoutes();
        return "Routes calculated and saved successfully";
    }

    @GetMapping("/find")
    public List<Route> findRoutes(@RequestParam UUID fromId, @RequestParam UUID toId) {
        return routeService.findRoutes(fromId, toId);
    }
}

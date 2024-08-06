package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.model.GraphEdge;
import com.bocklercode.cosmos_odyssey_core.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GraphController {

    private final GraphService graphService;

    @Autowired
    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/graph")
    public Map<String, List<GraphEdge>> getGraph() {
        return graphService.buildGraph();
    }
}

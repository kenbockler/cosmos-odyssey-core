package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.GraphEdge;
import com.bocklercode.cosmos_odyssey_core.model.GraphNode;
import com.bocklercode.cosmos_odyssey_core.model.ProviderLegRouteCombined;
import com.bocklercode.cosmos_odyssey_core.repository.ProviderLegRouteCombinedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    private final ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository;

    @Autowired
    public GraphService(ProviderLegRouteCombinedRepository providerLegRouteCombinedRepository) {
        this.providerLegRouteCombinedRepository = providerLegRouteCombinedRepository;
    }

    public Map<String, List<GraphEdge>> buildGraph() {
        List<ProviderLegRouteCombined> providers = providerLegRouteCombinedRepository.findAll();

        Map<String, List<GraphEdge>> graph = new HashMap<>();

        for (ProviderLegRouteCombined provider : providers) {
            String fromName = provider.getFromName();
            String toName = provider.getToName();

            if (isValidDirection(fromName, toName)) {
                GraphNode node = new GraphNode(
                        provider.getCompanyId(),
                        provider.getPrice(),
                        provider.getFlightStart(),
                        provider.getFlightEnd(),
                        provider.getDuration(),
                        provider.getDistance()
                );

                GraphEdge edge = new GraphEdge(fromName, toName, node);

                graph.putIfAbsent(fromName, new ArrayList<>());
                graph.get(fromName).add(edge);
            }
        }

        return graph;
    }

    private boolean isValidDirection(String fromName, String toName) {
        Set<String> validDirections = Set.of(
                "Mercury-Venus", "Venus-Mercury", "Venus-Earth", "Earth-Jupiter", "Earth-Uranus",
                "Mars-Venus", "Jupiter-Mars", "Jupiter-Venus", "Saturn-Earth", "Saturn-Neptune",
                "Uranus-Saturn", "Uranus-Neptune", "Neptune-Uranus", "Neptune-Mercury"
        );

        return validDirections.contains(fromName + "-" + toName);
    }
}

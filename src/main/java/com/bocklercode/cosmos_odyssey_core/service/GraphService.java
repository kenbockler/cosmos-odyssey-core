package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.ProviderLegRouteCombined;
import com.bocklercode.cosmos_odyssey_core.model.GraphEdge;
import com.bocklercode.cosmos_odyssey_core.model.GraphNode;
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

        // Lubatud teekonnad
        Set<String> validDirections = Set.of(
                "Mercury-Venus", "Venus-Mercury", "Venus-Earth", "Earth-Jupiter", "Earth-Uranus",
                "Mars-Venus", "Jupiter-Mars", "Jupiter-Venus", "Saturn-Earth", "Saturn-Neptune",
                "Uranus-Saturn", "Uranus-Neptune", "Neptune-Uranus", "Neptune-Mercury"
        );

        Map<String, List<GraphEdge>> graph = new HashMap<>();

        for (ProviderLegRouteCombined provider : providers) {
            String fromName = provider.getFromName();
            String toName = provider.getToName();

            if (provider.getFlightStart() != null && provider.getFlightEnd() != null &&
                    validDirections.contains(fromName + "-" + toName)) {

                GraphNode node = new GraphNode(
                        provider.getProviderId(),
                        provider.getCompanyName(),
                        provider.getPrice(),
                        provider.getFlightStart(),
                        provider.getFlightEnd(),
                        provider.getDuration(),
                        provider.getDistance()
                );

                graph.putIfAbsent(fromName, new ArrayList<>());
                List<GraphEdge> edges = graph.get(fromName);
                Optional<GraphEdge> existingEdge = edges.stream()
                        .filter(edge -> edge.getToName().equals(toName))
                        .findFirst();

                if (existingEdge.isPresent()) {
                    existingEdge.get().getNodes().add(node);
                } else {
                    List<GraphNode> nodes = new ArrayList<>();
                    nodes.add(node);
                    GraphEdge edge = new GraphEdge(fromName, toName, nodes);
                    edges.add(edge);
                }
            }
        }

        // Prindime graafi konsooli
        graph.forEach((from, edges) -> {
            System.out.println("From: " + from);
            edges.forEach(edge -> {
                System.out.println("  To: " + edge.getToName());
                edge.getNodes().forEach(node -> {
                    System.out.println("    Provider: " + node.getCompanyName() +
                            ", Price: " + node.getPrice() +
                            ", Start: " + node.getFlightStart() +
                            ", End: " + node.getFlightEnd() +
                            ", Duration: " + node.getDuration() + " ms" +
                            ", Distance: " + node.getDistance() + " km");
                });
            });
        });

        return graph;
    }
}

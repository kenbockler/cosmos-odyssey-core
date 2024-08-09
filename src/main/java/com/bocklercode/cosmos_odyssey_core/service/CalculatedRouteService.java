package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.CalculatedRoute;
import com.bocklercode.cosmos_odyssey_core.model.GraphEdge;
import com.bocklercode.cosmos_odyssey_core.model.GraphNode;
import com.bocklercode.cosmos_odyssey_core.repository.CalculatedRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculatedRouteService {

    private final CalculatedRouteRepository calculatedRouteRepository;

    @Autowired
    public CalculatedRouteService(CalculatedRouteRepository calculatedRouteRepository) {
        this.calculatedRouteRepository = calculatedRouteRepository;
    }

    public void findAndSaveShortestPath(Map<String, List<GraphEdge>> graph, String fromName, String toName) {
        PriorityQueue<List<GraphEdge>> queue = new PriorityQueue<>(Comparator.comparingLong(this::calculatePathDuration));

        // Algne teekond ilma tühja sõlmeta
        GraphEdge initialEdge = new GraphEdge(fromName, fromName, new ArrayList<>());
        queue.add(Collections.singletonList(initialEdge));

        Map<String, Instant> shortestPaths = new HashMap<>();
        shortestPaths.put(fromName, Instant.MIN);

        while (!queue.isEmpty()) {
            List<GraphEdge> path = queue.poll();
            GraphEdge lastEdge = path.get(path.size() - 1);

            // Kontrollime, et teekond ei vii tagasi alguspunkti
            if (lastEdge.getToName().equals(fromName)) {
                continue;
            }

            if (lastEdge.getToName().equals(toName)) {
                System.out.println("Saving shortest path from " + fromName + " to " + toName);
                saveCalculatedRoute(path);
                return;
            }

            for (GraphEdge edge : graph.getOrDefault(lastEdge.getToName(), Collections.emptyList())) {
                for (GraphNode node : edge.getNodes()) {
                    if (lastEdge.getNodes().isEmpty() ||
                            lastEdge.getNodes().get(lastEdge.getNodes().size() - 1).getFlightEnd().isBefore(node.getFlightStart().minus(Duration.ofMinutes(30)))) {  // 30 minuti ümberistumisaeg

                        List<GraphEdge> newPath = new ArrayList<>(path);
                        GraphEdge newEdge = new GraphEdge(edge.getFromName(), edge.getToName(), new ArrayList<>(List.of(node)));
                        newPath.add(newEdge);
                        queue.offer(newPath);

                        shortestPaths.put(edge.getToName(), node.getFlightEnd());

                        // Prindi tee konsooli
                        System.out.println("Path updated: ");
                        newPath.forEach(e -> {
                            e.getNodes().forEach(n -> {
                                System.out.println("  From: " + e.getFromName() + " To: " + e.getToName());
                                System.out.println("    Provider: " + n.getCompanyName() + ", Price: " + n.getPrice() +
                                        ", Start: " + n.getFlightStart() + ", End: " + n.getFlightEnd() +
                                        ", Duration: " + n.getDuration() + " ms, Distance: " + n.getDistance() + " km");
                            });
                        });
                    }
                }
            }
        }
    }

    private long calculatePathDuration(List<GraphEdge> path) {
        // Prindi teekonna algus ja lõpp
        System.out.println("Calculating path duration...");
        if (path.isEmpty() || path.get(0).getNodes().isEmpty() || path.get(path.size() - 1).getNodes().isEmpty()) {
            System.out.println("Path is empty or incomplete.");
            return Long.MAX_VALUE;
        }

        System.out.println("Path start: " + path.get(0).getNodes().get(0).getFlightStart());
        System.out.println("Path end: " + path.get(path.size() - 1).getNodes().get(path.get(path.size() - 1).getNodes().size() - 1).getFlightEnd());

        return Duration.between(
                path.get(0).getNodes().get(0).getFlightStart(),
                path.get(path.size() - 1).getNodes().get(path.get(path.size() - 1).getNodes().size() - 1).getFlightEnd()
        ).toMillis();
    }

    private void saveCalculatedRoute(List<GraphEdge> path) {
        // Prindi teekonna algus ja lõpp
        System.out.println("Saving calculated route...");
        if (path.isEmpty() || path.get(0).getNodes().isEmpty() || path.get(path.size() - 1).getNodes().isEmpty()) {
            System.out.println("Path is empty or incomplete.");
            return;
        }

        System.out.println("Path details at the start of saveCalculatedRoute:");
        path.forEach(edge -> {
            edge.getNodes().forEach(node -> {
                System.out.println("  From: " + edge.getFromName() + " To: " + edge.getToName());
                System.out.println("    Provider: " + node.getCompanyName() + ", Price: " + node.getPrice() +
                        ", Start: " + node.getFlightStart() + ", End: " + node.getFlightEnd() +
                        ", Duration: " + node.getDuration() + " ms, Distance: " + node.getDistance() + " km");
            });
        });

        String fromName = path.get(0).getFromName();
        String toName = path.get(path.size() - 1).getToName();

        String companyNames = path.stream()
                .flatMap(edge -> edge.getNodes().stream())
                .map(GraphNode::getCompanyName)
                .collect(Collectors.joining(", "));

        BigDecimal totalQuotedPrice = path.stream()
                .flatMap(edge -> edge.getNodes().stream())
                .map(GraphNode::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Instant totalTripStart = path.get(0).getNodes().get(0).getFlightStart();
        Instant totalTripEnd = path.get(path.size() - 1).getNodes().get(path.get(path.size() - 1).getNodes().size() - 1).getFlightEnd();

        long totalQuotedTravelTime = calculatePathDuration(path);
        long totalQuotedDistance = path.stream()
                .flatMap(edge -> edge.getNodes().stream())
                .mapToLong(GraphNode::getDistance)
                .sum();

        CalculatedRoute calculatedRoute = CalculatedRoute.builder()
                .calculatedRouteId(UUID.randomUUID())
                .fromName(fromName)
                .toName(toName)
                .companyNames(companyNames)
                .totalQuotedPrice(totalQuotedPrice)
                .totalTripStart(totalTripStart)
                .totalTripEnd(totalTripEnd)
                .totalQuotedTravelTime(totalQuotedTravelTime)
                .totalQuotedDistance(totalQuotedDistance)
                .build();

        calculatedRouteRepository.save(calculatedRoute);

        // Prindi teekonna detailid konsooli
        System.out.println("Calculated route from " + fromName + " to " + toName + ":");
        System.out.println("  Company Names: " + companyNames);
        System.out.println("  Total Quoted Price: " + totalQuotedPrice);
        System.out.println("  Total Trip Start: " + totalTripStart);
        System.out.println("  Total Trip End: " + totalTripEnd);
        System.out.println("  Total Quoted Travel Time: " + totalQuotedTravelTime + " ms");
        System.out.println("  Total Quoted Distance: " + totalQuotedDistance + " km");

        // Prindi kogu teekond
        System.out.println("Full path details:");
        for (GraphEdge edge : path) {
            for (GraphNode node : edge.getNodes()) {
                System.out.println("  From: " + edge.getFromName() + " to " + edge.getToName());
                System.out.println("    Provider: " + node.getCompanyName());
                System.out.println("    Price: " + node.getPrice());
                System.out.println("    Flight Start: " + node.getFlightStart());
                System.out.println("    Flight End: " + node.getFlightEnd());
                System.out.println("    Duration: " + node.getDuration() + " ms");
                System.out.println("    Distance: " + node.getDistance() + " km");
            }
        }
    }
}

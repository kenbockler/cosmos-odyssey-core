package com.bocklercode.cosmos_odyssey_core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphNode {
    private UUID companyId;
    private BigDecimal price;
    private Instant flightStart;
    private Instant flightEnd;
    private long duration;
    private long distance;
}

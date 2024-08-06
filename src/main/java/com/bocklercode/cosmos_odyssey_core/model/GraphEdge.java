package com.bocklercode.cosmos_odyssey_core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdge {
    private String fromName;
    private String toName;
    private GraphNode node;
}

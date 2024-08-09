package com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
    private String id;
    private String validUntil;
    private List<Leg> legs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Leg {
        private String id;
        private RouteInfo routeInfo;
        private List<Provider> providers;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RouteInfo {
            private String id;
            private Location from;
            private Location to;
            private long distance;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Location {
                private String id;
                private String name;
            }
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Provider {
            private String id;
            private Company company;
            private double price;
            private String flightStart;
            private String flightEnd;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Company {
                private String id;
                private String name;
            }
        }
    }
}

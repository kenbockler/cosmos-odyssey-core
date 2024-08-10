package com.bocklercode.cosmos_odyssey_core.service;

import com.bocklercode.cosmos_odyssey_core.model.model_travel_prices_api.APIResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class APIClient {

    private static final String API_URL = "https://cosmos-odyssey.azurewebsites.net/api/v1.0/TravelPrices";

    public APIResponse getTravelPrices() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(API_URL, APIResponse.class);
    }
}
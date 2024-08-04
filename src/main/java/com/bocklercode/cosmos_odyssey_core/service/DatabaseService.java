package com.bocklercode.cosmos_odyssey_core.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS provider_leg_pricelist_combined AS SELECT p.provider_id, p.company_id, p.company_name, p.price, p.flight_start, p.flight_end, p.duration, l.leg_id, l.route_id, l.from_id, l.from_name, l.to_id, l.to_name, l.distance, pr.pricelist_id, pr.valid_until, pr.created_at FROM providers p JOIN legs l ON p.leg_id = l.leg_id JOIN pricelists pr ON l.pricelist_id = pr.pricelist_id;";
        jdbcTemplate.execute(sql);
    }
}


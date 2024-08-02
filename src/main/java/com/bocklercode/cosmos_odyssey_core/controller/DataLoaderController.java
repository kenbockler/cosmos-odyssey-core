package com.bocklercode.cosmos_odyssey_core.controller;

import com.bocklercode.cosmos_odyssey_core.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataLoaderController {

    @Autowired
    private DataLoaderService dataLoaderService;

    @GetMapping("/loadData")
    public String loadData() {
        dataLoaderService.loadData();
        return "Data loaded successfully";
    }
}

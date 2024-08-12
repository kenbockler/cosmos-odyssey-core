package com.bocklercode.cosmos_odyssey_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CosmosOdysseyCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosmosOdysseyCoreApplication.class, args);
	}
}
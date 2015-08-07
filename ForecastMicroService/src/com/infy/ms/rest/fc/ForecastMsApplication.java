package com.infy.ms.rest.fc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ForecastMsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ForecastMsApplication.class, args);
	}

}

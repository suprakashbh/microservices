package com.infy.microservice.support;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.amqp.EnableTurbineAmqp;

@SpringBootApplication
@EnableTurbineAmqp
@EnableDiscoveryClient
public class TurbineServerApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TurbineServerApp.class).run(args);
    }
}

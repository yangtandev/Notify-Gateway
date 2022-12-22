package com.gini.notifyGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotifyGatewaySpringBootApp extends SpringBootServletInitializer {
    public static final Logger logger = LoggerFactory.getLogger(NotifyGatewaySpringBootApp.class);

    public static void main(String[] args) {
        SpringApplication.run(NotifyGatewaySpringBootApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NotifyGatewaySpringBootApp.class);
    }

    @Bean
    public CommandLineRunner initData() {
        return (args) -> {

        };
    }
}

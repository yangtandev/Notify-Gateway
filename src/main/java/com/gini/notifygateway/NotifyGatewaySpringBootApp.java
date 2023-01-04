package com.gini.notifygateway;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
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

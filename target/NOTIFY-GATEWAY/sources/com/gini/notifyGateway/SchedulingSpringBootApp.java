package com.gini.notifyGateway;

import com.gini.notifyGateway.model.CalendarRequest;
import com.gini.notifyGateway.utils.CalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.LocalDateTime;
import java.util.Properties;

@SpringBootApplication
public class SchedulingSpringBootApp extends SpringBootServletInitializer {
    public static final Logger logger = LoggerFactory.getLogger(SchedulingSpringBootApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SchedulingSpringBootApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SchedulingSpringBootApp.class);
    }

    @Bean
    public CommandLineRunner initData() {
        return (args) -> {

        };
    }
}

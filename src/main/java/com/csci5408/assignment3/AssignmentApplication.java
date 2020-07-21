package com.csci5408.assignment3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/*
 * Spring boot started class, serving as an entry point to this application.
 * Simply run the main method which will spawn an embedded server listening on http://localhost:9090/
 * Configurations are defined in application.properties file in resource directory.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.csci5408.assignment3.*"})
public class AssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
        System.out.println("CSCI-5408 assignment-3 application started");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
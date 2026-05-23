package com.devconnect.devconnectbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EntityScan(basePackages = {"model", "model"})
@EnableJpaRepositories(basePackages = {"com.devconnect.devconnectbackend.repository"})
public class DevconnectBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevconnectBackendApplication.class, args);
    }
}
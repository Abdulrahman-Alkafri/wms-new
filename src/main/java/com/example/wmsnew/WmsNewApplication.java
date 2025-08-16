package com.example.wmsnew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WmsNewApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsNewApplication.class, args);
    }

}

package com.example.wmsnew;

import com.example.wmsnew.Config.Auth0Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(Auth0Properties.class)
public class WmsNewApplication {

  public static void main(String[] args) {
    SpringApplication.run(WmsNewApplication.class, args);
  }
}

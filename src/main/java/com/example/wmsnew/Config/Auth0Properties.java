package com.example.wmsnew.Config;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {

    @NotBlank(message = "Domain is required")
    private String domain;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotBlank(message = "Client Secret is required")
    private String clientSecret;

    @NotBlank(message = "Audience is required")
    private String audience;

    private String connection;
}

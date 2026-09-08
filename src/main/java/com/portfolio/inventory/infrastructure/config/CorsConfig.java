package com.portfolio.inventory.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    // Permitir credenciales del navegador
    config.setAllowCredentials(true);

    // Angular
    config.setAllowedOrigins(
      Arrays.asList("http://localhost:4200")
    );

    // Headers que puede enviar Angular
    config.setAllowedHeaders(
      Arrays.asList(
        "Origin",
        "Content-Type",
        "Accept",
        "Authorization"
      )
    );

    // Métodos HTTP permitidos
    config.setAllowedMethods(
      Arrays.asList(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "PATCH",
        "OPTIONS"
      )
    );

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
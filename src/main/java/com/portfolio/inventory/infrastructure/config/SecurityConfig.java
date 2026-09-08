package com.portfolio.inventory.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableSpringDataWebSupport
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
      // Habilitar CORS
      .cors(Customizer.withDefaults())

      // Como es una API REST con JWT
      .csrf(csrf -> csrf.disable())

      // No usar sesiones
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )

      .authorizeHttpRequests(authz -> authz

        // IMPORTANTE:
        // permitir el preflight CORS
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Rutas públicas
        .requestMatchers(
          "/api/public/**",
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html"
        ).permitAll()

        // Todo lo demás requiere autenticación
        .anyRequest().authenticated()
      )

      // JWT / Supabase
      .oauth2ResourceServer(oauth2 ->
        oauth2.jwt(Customizer.withDefaults())
      );

    return http.build();
  }
}
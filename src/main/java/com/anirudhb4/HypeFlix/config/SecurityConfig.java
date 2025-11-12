package com.anirudhb4.HypeFlix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // --- THIS IS THE FIX ---
                        // Allow anyone to make a GET request to /api/movies
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()

                        // We'll also pre-configure our other public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/leaderboard/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/discussion/**").permitAll()

                        // All other requests (like POST) must be authenticated
                        .anyRequest().authenticated()
                )

                // Configure this as a stateless OAuth 2.0 Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Disable CSRF because we are using a stateless API (JWTs)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
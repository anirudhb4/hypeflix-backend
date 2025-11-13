package com.anirudhb4.HypeFlix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- YOUR JWT SECRET ---
    // (We can put the @Value back later, hardcoding is fine for testing)
    @Value("${hypeflix.security.jwt-secret}")
    private String jwtSecret;
    // private String jwtSecret = "PASTE_YOUR_SECRET_HERE_FOR_TESTING";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. ADD THIS TO ENABLE CORS WITHIN THE SECURITY CHAIN
                .cors(Customizer.withDefaults())

                // 2. DISABLE CSRF (STAYS THE SAME)
                .csrf(csrf -> csrf.disable())

                // 3. SET STATELESS SESSION (STAYS THE SAME)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. SET UP AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests (for preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Allow GET requests for movies/leaderboard
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/leaderboard/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/movies/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/**").authenticated()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )

                // 5. CONFIGURE AS OAUTH2 RESOURCE SERVER (STAYS THE SAME)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // 6. ADD THIS BEAN TO CONFIGURE CORS RULES
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow your React app (Vite)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Allow all standard methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (not strictly needed for Bearer token, but good)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }

    // 7. YOUR JWT DECODER BEAN (STAYS THE SAME)
    @Bean
    public JwtDecoder jwtDecoder() {
        var secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
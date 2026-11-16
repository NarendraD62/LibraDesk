package com.libradesk.api.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Public demo API security defaults and configurable development CORS. */
@Configuration
public class SecurityConfiguration {
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(auth->auth.anyRequest().permitAll()).build();}
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${libradesk.cors.allowed-origins:http://localhost:3000}") String origins){CorsConfiguration config=new CorsConfiguration();config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","X-Request-Id"));config.setAllowCredentials(true);UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/api/**",config);return source;}
}

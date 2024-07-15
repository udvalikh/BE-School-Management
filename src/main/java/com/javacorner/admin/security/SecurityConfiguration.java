package com.javacorner.admin.security;

import com.javacorner.admin.filter.JWTAuthenticationFilter;
import com.javacorner.admin.helper.JWTHelper;
import com.javacorner.admin.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserService userService;

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Create the JWT filter and set the authentication manager
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtHelper());
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)));

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Use stateless sessions
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/authenticate").permitAll()  // Allow unauthenticated access to authenticate endpoint
                        .requestMatchers("/refresh-token/**").permitAll()  // Allow unauthenticated access to refresh-token endpoint
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add the JWT filter before the default UsernamePasswordAuthenticationFilter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JWTHelper jwtHelper() {
        return new JWTHelper();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));  // Frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));  // HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));  // Allowed headers
        configuration.setAllowCredentials(true);  // Allow credentials
        configuration.setMaxAge(3600L);  // Set the max age for preflight requests
        return request -> configuration;
    }

}

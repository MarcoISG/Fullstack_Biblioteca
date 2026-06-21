package com.duoc.prestamos.config;

import com.duoc.prestamos.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.security.user.name}")
    private String appUsername;

    @Value("${app.security.user.password}")
    private String appPassword;

    @Value("${app.security.user.role}")
    private String appRole;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "No autorizado"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, HttpStatus.FORBIDDEN, "Acceso denegado"))
                )
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(appUsername)
                .password(passwordEncoder().encode(appPassword))
                .roles(appRole)
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    HttpStatus status,
                                    String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                null
        );

        JsonMapper.builder()
                .findAndAddModules()
                .build()
                .writeValue(response.getOutputStream(), body);
    }
}

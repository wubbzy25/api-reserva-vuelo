package com.api.reservavuelos.Config;


import com.api.reservavuelos.Filters.JwtValidationFilter;
import com.api.reservavuelos.Filters.URLFilter;
import com.api.reservavuelos.Security.JwtAuthenticationFilter;
import com.api.reservavuelos.Security.jwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity()
public class SecurityConfig {

    private final jwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final URLFilter urlFilter;
    private final JwtValidationFilter jwtvalidationFilter;

    @Autowired
    public SecurityConfig(jwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          URLFilter urlFilter,
                          JwtValidationFilter jwtvalidationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.urlFilter = urlFilter;
        this.jwtvalidationFilter = jwtvalidationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity Http) throws Exception {
        Http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/v1/vuelos/vuelo/**").hasAuthority("administrador")
                        .requestMatchers(HttpMethod.PUT, "api/v1/vuelos/vuelo/**").hasAuthority("administrador")
                        .requestMatchers(HttpMethod.DELETE, "api/v1/vuelos/vuelo/**").hasAuthority("administrador")
                        .anyRequest().authenticated()
                );
                Http.addFilterBefore(urlFilter, UsernamePasswordAuthenticationFilter.class);
                Http.addFilterBefore(jwtvalidationFilter, UsernamePasswordAuthenticationFilter.class);
                Http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return Http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

package com.stevedutch.intellectron.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // Erlaube den Zugriff auf die Startseite ("/") und statische Ressourcen
                .requestMatchers("/", "/index", "/welcome", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                // Jede andere Anfrage (anyRequest) muss authentifiziert sein
                .anyRequest().authenticated()
            )
            // CSRF nur für API-Endpunkte deaktivieren, nicht für Formulare
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/zettel/**", "/input/**") // Nur für AJAX-API-Endpunkte
            )
            // Konfiguriere Form-basierten Login
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            // Konfiguriere OAuth2 Login
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // Spezifiziert die benutzerdefinierte Login-Seite
            )
            // Konfiguriere den Logout
            .logout((logout) -> logout
                .logoutSuccessUrl("/logout-success") // Nach dem Logout zur Bestätigungsseite weiterleiten
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

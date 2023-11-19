package com.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurity {

    // UserDetailsService for loading user details during authentication
    private final UserDetailsService userDetailsService;

    // Bean definition for PasswordEncoder
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain configuration for HTTP security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Disable CSRF protection
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").permitAll() // Allow access to registration endpoint
                                .requestMatchers("/index").permitAll() // Allow access to /index
                                .requestMatchers("/users").hasRole("ADMIN") // Allow access to /users for users with ADMIN role
                ).formLogin(
                        form -> form
                                .loginPage("/login") // Custom login page URL
                                .loginProcessingUrl("/login") // URL for processing login requests
                                .defaultSuccessUrl("/users") // Default URL after successful login
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL for processing logout requests
                                .permitAll()
                );
        return http.build();
    }

    // Global configuration for authentication
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService) // Set UserDetailsService for authentication
                .passwordEncoder(passwordEncoder()); // Set PasswordEncoder for password handling
    }
}
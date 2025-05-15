package com.LMS.Learning_Management_System.config;

import com.LMS.Learning_Management_System.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // This method replaces the `WebSecurityConfigurerAdapter`
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for non-browser clients (APIs)
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/login").permitAll()  // Allow login endpoint without authentication
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Restrict access to ADMIN role
                .requestMatchers("/api/instructor/**").hasRole("INSTRUCTOR")  // Restrict access to INSTRUCTOR role
                .requestMatchers("/api/student/**").hasRole("STUDENT")  // Restrict access to STUDENT role
                .anyRequest().authenticated()  // Protect all other endpoints
                .and()
                .httpBasic();  // Use HTTP Basic authentication
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    // Password encoder to encode passwords using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

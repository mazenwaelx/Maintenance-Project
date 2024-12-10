package com.LMS.Learning_Management_System.conifg;


import com.LMS.Learning_Management_System.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//annotation marks this class as a configuration class,
// meaning it will define beans and configure settings for
// the application annotation marks this class as a configuration class, meaning
// it will define beans and configure settings for the application
//------------
//The WebSecurityConfig class configures Spring Security to:
//Define publicly accessible URLs.
//Set up custom authentication and authorization rules.
//Handle login and logout with custom success handling.
//Securely hash passwords with BCrypt.
@Configuration
public class WebSecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    private final String[] publicUrl = {"/",
            "/global-search/**", "/api/users/register", "/register/**", "/webjars/**", "/resources/**", "/assets/**",
            "/css/**", "/summernote/**", "/js/**", "/*.css", "/*.js", "/*.js.map", "/fonts**", "/favicon.ico",
            "/resources/**", "/error"};

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicUrl).permitAll();
            auth.anyRequest().authenticated();
        });

        // Configure form login for an API backend
        http.formLogin(form -> form.loginProcessingUrl("/api/users/login").permitAll()
                        .successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/"))
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                        .rememberMeParameter("remember-me"));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

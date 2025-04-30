package com.spring.ems.config;

import com.spring.ems.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public AppSecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // 🔓 Public Endpoints
                .requestMatchers(HttpMethod.POST, "/user-api/user", "/user-api/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/user-api/send-otp","/user-api/verify-otp","user-api/reset-password").permitAll()

                // 👤 USER + ADMIN - Notifications (specific user actions FIRST)
                .requestMatchers("/notifications-api/soft-delete/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/notifications-api/user/**").hasAnyRole("USER", "ADMIN")

                // 🎫 USER + ADMIN - Ticket related endpoints
                .requestMatchers("/tickets-api/book", "/tickets-api/cancel/**", "/tickets-api/user", "/tickets-api/user/cancelled","/tickets-api/{ticketId}/details")
                .hasAnyRole("USER", "ADMIN")

                // 📅 USER + ADMIN - View & search events
                .requestMatchers("/events-api/view", "/events-api/search", "/events-api/upcoming", "/events-api/past", "/events-api/category/**")
                .hasAnyRole("USER", "ADMIN")

                // ⭐ USER + ADMIN - Feedback
                .requestMatchers("/feedback-api/submit", "/feedback-api/user/**", "/feedback-api/event/**")
                .hasAnyRole("USER", "ADMIN")

                // 👮 USER/Admin-specific account access
//                .requestMatchers(HttpMethod.PUT, "/user-api/{id}").hasAnyRole("USER", "ADMIN")
//                .requestMatchers(HttpMethod.GET, "/user-api/view", "/user-api/{id}","user-api/{userId}/analytics").hasRole("ADMIN")

                // 👤 USER-only endpoints
                .requestMatchers(HttpMethod.GET, "/events-api/{id}").hasRole("USER")
//                .requestMatchers(HttpMethod.GET, "/user-api/email/{email}","/user-api/{id}").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/user-api/view", "/user-api/{userId}/analytics").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/user-api/{id}", "/user-api/email/{email}","/events-api/{id}","/events-api/internal/{eventId}").hasAnyRole("USER", "ADMIN")
                

                // 🛡️ ADMIN-only section for full control
                .requestMatchers(
                    "/events-api/**",
                    "/tickets-api/view",
                    "/tickets-api/event/**",
                    "/notifications-api/**",
                    "/feedback-api/**"
                ).hasRole("ADMIN")

                // 🚫 Anything else requires authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // ✅ CORS for frontend on port 5173 (Vite)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 🔐 Auth provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    // 🔐 Manager for authentication flow
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🔑 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

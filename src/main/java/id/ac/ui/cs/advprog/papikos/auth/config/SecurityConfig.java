package id.ac.ui.cs.advprog.papikos.auth.config;

import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless session (JWT based)
                .csrf(csrf -> csrf.disable())

                // Setup authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/management.html",
                                "/houseDetails.html",
                                "/rentalRequests.html",
                                "/inbox.html",
                                "/admin.html",
                                "/rental.html",
                                "/RentalHouseDetails.html",
                                // <-- allow SockJS handshake & WS connect
                                "/ws/**",
                                // <-- allow topic subscriptions over HTTP fallback if used
                                "/topic/**",
                        // Landlord-only REST
                                "/houseDetails.html",
                                "/dashboard.html",
                                "/wallet-topup.html",
                                "/wallet-pay.html",
                                "/transaction-history.html",
                                "/wallet-history.html",
                                "/js/**",
                                "/api/auth/**",
                                "/api/payment/**",
                                "/api/wallet/**",
                                "/api/transaction/**"
                                ).permitAll()  // Public endpoints (e.g., registration, login)
                        .requestMatchers("/api/management/**").hasRole("LANDLORD")

                        // Admin-only
                        .requestMatchers("/api/auth/users/**").hasRole("ADMIN")

                        // Tenant-only
                        .requestMatchers("/api/boarding-houses/**").hasRole("TENANT")

                        // everything else needs a valid JWT
                        .anyRequest().authenticated()
                )

                // Stateless sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Send 401 on auth failures
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        // Add your JWT filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

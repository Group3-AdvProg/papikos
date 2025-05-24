package id.ac.ui.cs.advprog.papikos.auth.config;

import id.ac.ui.cs.advprog.papikos.auth.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;                      // ← import
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        /* ---------- public URLs ---------- */
                        .requestMatchers(
                                "/actuator/**", "/actuator/prometheus",
                                "/api/auth/**",   // login, register, refresh
                                "/ws/**",         // sockjs handshake
                                "/topic/**",      // stomp fallback
                                "/css/**", "/js/**", "/images/**",
                                "/*.html", "/"
//                                "/login.html", "/register.html",
//                                "/management.html", "/houseDetails.html", "/rental.html",
//                                "/rentalRequests.html", "/inbox.html", "/admin.html",
//                                "/dashboard.html", "/wallet-*.html", "/transaction-history.html"
                        ).permitAll()

                        .requestMatchers("/api/auth/users/me").authenticated()
                        /* ---------- management & boarding-houses ---------- */
                        .requestMatchers("/api/management/**").hasRole("LANDLORD")
                        .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/boarding-houses/**").hasRole("TENANT")
                        .requestMatchers("/api/notifications/broadcast").hasRole("ADMIN")

                        .requestMatchers("/api/auth/**").permitAll()

                        /* ---------- chat: only TENANT can create rooms ---------- */
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms")
                        .hasRole("TENANT")

                        /* ---------- all other chat APIs require authentication ---------- */
                        .requestMatchers("/api/chat/**")
                        .authenticated()

                        /* ---------- everything else ---------- */
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        // insert your JWT filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

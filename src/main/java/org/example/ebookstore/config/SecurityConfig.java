package org.example.ebookstore.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/change-currency"))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        .requestMatchers("/admin-panel").hasRole("ADMIN")

                        .requestMatchers("/", "/home", "/users/login", "/users/register", "/about",
                                "/customer-service", "/books/*", "/publishers/*", "/authors/*",
                                "categories/*", "/change-currency", "/change-currency/*", "/fx-rates",
                                "/search/**", "/search", "/users/logout").permitAll()
                        .requestMatchers("/css/**", "/javascript/**", "/images/**", "/icons/**",
                                "/stars/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .usernameParameter("email")
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/perform_login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/users/login?error=true"))
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true))
                .build();
    }
}

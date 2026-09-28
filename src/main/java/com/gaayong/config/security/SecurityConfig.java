package com.gaayong.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;
    private final String rememberMeKey;

    public SecurityConfig(DataSource dataSource, @Value("${REMEMBER_ME_KEY:change-this-remember-me-key}") String rememberMeKey) {
        this.dataSource = dataSource;
        this.rememberMeKey = rememberMeKey;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        PersistentTokenBasedRememberMeServices services =
                new PersistentTokenBasedRememberMeServices(rememberMeKey, userDetailsService, tokenRepository());
        services.setTokenValiditySeconds(60 * 60 * 24 * 30);
        services.setParameter("rememberMe");
        return services;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RememberMeServices rememberMeServices) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/logout",
                                "/signin",
                                "/signup",
                                "/index.html",
                                "/assets/**",
                                "/css/**",
                                "/icon/**",
                                "/.well-known/**",
                                "/manifest.json"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(remember -> remember
                        .rememberMeServices(rememberMeServices)
                        .key(rememberMeKey)
                        .tokenRepository(tokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 30))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            if (req.getRequestURI().startsWith("/api/")) {
                                res.setStatus(401);
                                res.setContentType("application/json;charset=UTF-8");
                                res.getWriter().write("{\"error\":\"인증이 필요합니다.\"}");
                            } else {
                                res.sendRedirect("/signin");
                            }
                        }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/signin?invalid=true")
                        .sessionFixation().migrateSession()
                );

        return http.build();
    }
}

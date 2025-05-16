package com.gaayong.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/", "/signin", "/signup", "/css/**", "/js/**", "/icon/**", "/.well-known/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form
                        .loginPage("/signin")
                        .loginProcessingUrl("/signin")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/signin?error=true")
                        .usernameParameter("userNm")
                        .passwordParameter("pwd")
                        .permitAll())
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey")
                        .tokenRepository(tokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 30) // 30일간 유효
                        .rememberMeParameter("remember-me"))
                .logout(logout -> logout
                        .logoutUrl("/signout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/signin?invalid=true")
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/signin?expired=true")
                );

        return http.build();
    }
}

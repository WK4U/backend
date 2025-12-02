package com.workforyou.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * Configuração de segurança Spring Security 6.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de autenticação (públicos)
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/esqueceu-senha").permitAll()
                        .requestMatchers("/auth/validar-pin").permitAll()
                        .requestMatchers("/auth/redefinir-senha").permitAll()

                        // GET abertos de postagens
                        .requestMatchers(HttpMethod.GET, "/postagem/getAll").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagem/get/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagem/get").permitAll()
                        .requestMatchers(HttpMethod.GET, "/postagem/getPorTipoServico").permitAll()

                        // (Opcional) permitir arquivos estáticos ou health checks
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                        // Qualquer outra rota requer autenticação
                        .anyRequest().authenticated()
                )
                // Filtro JWT antes do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
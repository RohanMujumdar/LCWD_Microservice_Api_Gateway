package com.gateway.api_gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity)
    {

        httpSecurity.cors(e->e.disable())
                .csrf(e->e.disable())
                .authorizeExchange(exchange->{
                    exchange.pathMatchers(HttpMethod.GET, "/food/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.POST, "/restaurant/**").hasRole("ADMIN")
                            .anyExchange().authenticated();
                })
                .oauth2ResourceServer(config->
                        config.jwt(Customizer.withDefaults()))

        ;
        return httpSecurity.build();
    }
}

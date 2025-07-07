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

//               The app is configuring OAuth2 Resource Server support.
//               It is using JWT (JSON Web Token) for authentication.
//               Customizer.withDefaults() applies the default settings for
//               JWT handling (like decoding tokens using a configured JwtDecoder).

//               In short: Enable JWT-based authentication with default settings.
                .oauth2ResourceServer(config->
                        config.jwt(Customizer.withDefaults()))

        ;
        return httpSecurity.build();
    }
}

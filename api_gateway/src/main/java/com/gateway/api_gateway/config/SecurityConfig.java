package com.gateway.api_gateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private RoleConverter roleConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity)
    {

        httpSecurity.cors(e->e.disable())
                .csrf(e->e.disable())
                .authorizeExchange(exchange->{
                    exchange.pathMatchers(HttpMethod.GET).permitAll()
                            .pathMatchers("/food/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.POST, "/restaurant/**").hasRole("ADMIN")
                            .anyExchange().authenticated();
                })

//               The app is configuring OAuth2 Resource Server support.
//               It is using JWT (JSON Web Token) for authentication.
//               Customizer.withDefaults() applies the default settings for
//               JWT handling (like decoding tokens using a configured JwtDecoder).

//               In short: Enable JWT-based authentication with default settings.
//                .oauth2ResourceServer(config->
//                        config.jwt(Customizer.withDefaults()))

                .oauth2ResourceServer(config->
                        config.jwt(jwt-> jwt.jwtAuthenticationConverter(roleExtractor()))
                );


        return httpSecurity.build();
    }


    public Converter<Jwt, Mono<AbstractAuthenticationToken>> roleExtractor()
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roleConverter);


//      Converts a normal JwtAuthenticationConverter object to a reactive one.
//      This is because our project is reactive.
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}

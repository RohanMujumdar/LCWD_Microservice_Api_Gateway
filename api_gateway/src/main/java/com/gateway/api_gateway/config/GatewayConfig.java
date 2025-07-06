package com.gateway.api_gateway.config;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    // We are creating the routes to different services via Java Beans configs over here
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder)
    {
        return builder.routes().
                route(
                        "foodService",
                        route->route.path("/food/**")
                                .filters(f -> f.rewritePath("/food/?(?<remaining>.*)", "/$\\{remaining}")
                                        .circuitBreaker(circuitBreakerConfig -> circuitBreakerConfig.setName("circuitBreakerFood")
                                                .setFallbackUri("forward:/circuitBreaker/fallback")
                                        )
                                        .requestRateLimiter(rateConfig->{
//                                            Fill the implementation of rate limiter.
                                            rateConfig.setRateLimiter(rateLimiter())
//                                            Fill the implementation of key resolver.
                                                    .setKeyResolver(keyResolver());
                                        })

                                )
                                .uri("lb://FOODSERVICE")
                ).
                route(
                        "restaurantService",
                        route->route.path("/restaurant/**")
                                .filters(f -> f.rewritePath("/restaurant/?(?<remaining>.*)", "/$\\{remaining}")
                                        .retry(retryConfig -> {
                                            retryConfig.setRetries(3)
                                                    .setMethods(HttpMethod.GET)
                                                    .setBackoff(

//                                                  After this much time, 1st retry will take place.
                                                            Duration.ofMillis(100),

//                                                  Final retry will go upto this much time
                                                            Duration.ofMillis(800),

//                                                  Duration of every retry will increase by the below factor
                                                            2,

//                                                  The duration will increase based on the previous value.
                                                            true
                                                    );
                                        }))
                                .uri("lb://RESTAURANTSERVICE")
                ).
                route(
                        "orderService",
                        route->route.path("/orders/**")
                                .filters(f -> f.rewritePath("/orders/?(?<remaining>.*)", "/$\\{remaining}")
                                        .addRequestHeader("Authorization", "Bearer xyz123"))
                                .uri("lb://ORDERSERVICE")
                ).
                build();

    }



    @Bean
    public KeyResolver keyResolver()
    {
        return exchange -> {
            return Mono.just(
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
            );
        };
    }

    @Bean
    public RedisRateLimiter rateLimiter()
    {
        return new RedisRateLimiter(1, 60, 60);
    }

}


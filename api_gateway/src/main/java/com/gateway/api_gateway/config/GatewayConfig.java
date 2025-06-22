package com.gateway.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                .filters(f -> f.rewritePath("/food/?(?<remaining>.*)", "/$\\{remaining}"))
                                .uri("lb://FOODSERVICE")
                ).
                route(
                        "restaurantService",
                        route->route.path("/restaurant/**")
                                .filters(f -> f.rewritePath("/restaurant/?(?<remaining>.*)", "/$\\{remaining}"))
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
}

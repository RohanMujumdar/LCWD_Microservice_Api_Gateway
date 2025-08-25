package com.gateway.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallBackController {


    // This is the fallback mechanism
    @RequestMapping("/circuitBreaker/fallback")
    public Mono<ResponseEntity<String>> circuitBreakerFoodFallback()
    {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("The particular Service is down. Contact the service provider."));
    }
}

package com.example.demo.controller;

import com.example.demo.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @Value("${server.instance}")
    private String instance;
    @Value("${server.port}")
    private String port;

    @GetMapping("/metrics")
    public Map<String, Object> metrics() { // key is string value can be anything (Object)
        return Map.of("instance", instance, "port", port, "requestsServed", metricsService.getRequestsServed());
    }
}
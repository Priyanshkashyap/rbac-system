package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {

    private final AtomicLong requestsServed = new AtomicLong();

    public void incrementRequests() {
        requestsServed.incrementAndGet();
    }

    public long getRequestsServed() {
        return requestsServed.get();
    }
}
package com.example.demo.config;

import com.example.demo.service.MetricsService;
import jakarta.servlet.http.HttpServletRequest; // Represents the incoming HTTP request towards controller
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// a filter config tells How many HTTP requests reached my server? while interceptor tells How many controller requests did my application process?
@Component
@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {

    private final MetricsService metricsService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  //Spring calls this method before the controller method executes
    {
        metricsService.incrementRequests();
        return true;
    }
}
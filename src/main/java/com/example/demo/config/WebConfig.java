package com.example.demo.config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer { // This class is what registers your interceptor with Spring MVC. Without it, your MetricsInterceptor bean exists, but Spring will never execute it for incoming requests.

    private final MetricsInterceptor metricsInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(metricsInterceptor);
    }
}
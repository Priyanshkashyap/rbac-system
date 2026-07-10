package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    @Value("${server.instance}")
    private String instanceName;
    @Value("${server.port}")
    private String port;

    @GetMapping("/instance")
    public String getInstance() {
        return "Backend running on port " + port + ": " + instanceName;
    }
}
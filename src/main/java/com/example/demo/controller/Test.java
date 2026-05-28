package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// One controller = one class & Class name should be Capitalized
// Spring Security is ON by default since we downloaded the security package this disable it temporarily in config
@RestController
@RequestMapping("/api/test")
    public class Test {
        @GetMapping
        public String test() {
            return "Backend Working 🚀";
        }
    }

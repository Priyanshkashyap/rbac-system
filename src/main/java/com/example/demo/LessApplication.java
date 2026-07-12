package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class LessApplication {

	public static void main(String[] args) {
		SpringApplication.run(LessApplication.class, args);
	}
	@Autowired
	Environment env;

	@PostConstruct
	public void printDb() {
		System.out.println("DB = " + env.getProperty("spring.datasource.url"));
	}

}

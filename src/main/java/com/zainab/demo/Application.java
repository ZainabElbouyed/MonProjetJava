package com.zainab.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Application Spring Boot démarrée !");
    }
    
    @GetMapping("/")
    public String home() {
        return "Bienvenue sur l'application de Zainab - Déployée par Jenkins!";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello depuis Jenkins Pipeline!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK - Application fonctionnelle";
    }
}
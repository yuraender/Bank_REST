package com.example.bankcards;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BankApplication {

    @Getter
    private static ApplicationContext instance;

    public static void main(String[] args) {
        instance = SpringApplication.run(BankApplication.class, args);
    }
}

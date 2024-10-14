package com.salary.management.balance_calculator_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BalanceCalculatorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalanceCalculatorServiceApplication.class, args);
    }
}

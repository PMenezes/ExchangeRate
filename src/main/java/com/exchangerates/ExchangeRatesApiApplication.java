package com.exchangerates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.exchangerates")
@EnableCaching
public class ExchangeRatesApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatesApiApplication.class, args);
    }
}

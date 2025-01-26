package com.exchangerates;

import com.exchangerates.config.ExternalApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.exchangerates")
@EnableCaching
@EnableConfigurationProperties(ExternalApiProperties.class)
public class ExchangeRatesApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatesApiApplication.class, args);
    }
}

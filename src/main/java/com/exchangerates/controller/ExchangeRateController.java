package com.exchangerates.controller;

import com.exchangerates.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/fetch")
    public String fetchAndStoreExchangeRate(@RequestParam String base, @RequestParam String target) {
        exchangeRateService.fetchAndSaveExchangeRate(base, target);
        return "Exchange rate fetched and stored successfully!";
    }
}


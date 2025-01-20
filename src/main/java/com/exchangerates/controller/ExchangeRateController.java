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

    @GetMapping("/rate")
    public Double getExchangeRate(@RequestParam String fromCurrency, @RequestParam String toCurrency) {
        return exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
    }
}


package com.exchangerates.controller;

import com.exchangerates.dto.ExchangeRateDto;
import com.exchangerates.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    public Double getExchangeRate(@RequestParam("from") String fromCurrency, @RequestParam("to") String toCurrency) {
        return exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all exchange rates for a given currency")
    public ResponseEntity<Map<String, Object>> getAllExchangeRates(@RequestParam("from") String fromCurrency) {
        try {
            ExchangeRateDto rates = exchangeRateService.getAllExchangeRates(fromCurrency);
            Map<String, Object> response = new HashMap<>();
            response.put("base", rates.getSource());
            response.put("rates", rates.getQuotes());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @Operation(summary = "Convert amount from one currency to another",
            description = "Takes 'from', 'to', and 'amount' as query parameters and returns the converted value.")
    @GetMapping("/convert")
    public Double convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {
        return exchangeRateService.convertCurrency(from, to, amount);
    }

    @Operation(
            summary = "Convert amount from one currency to multiple currencies",
            description = "Takes 'from', 'toCurrencies' (comma-separated), and 'amount' as query parameters and returns the converted values."
    )
    @GetMapping("/convert-multiple")
    public Map<String, Double> convertCurrencyToMultiple(
            @RequestParam String from,
            @RequestParam String toCurrencies, // Comma-separated currency codes
            @RequestParam double amount) {
        return exchangeRateService.convertCurrencyToMultiple(from, toCurrencies, amount);
    }
}


package com.exchangerates.controller;

import com.exchangerates.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate")
    @Operation(summary = "Get exchange rate from a given currency")
    public ResponseEntity<Double> getExchangeRate(@RequestParam("from") String fromCurrency, @RequestParam("to") String toCurrency) {
        try {
            Double rate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Double.NaN);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all exchange rates for a given currency")
    public ResponseEntity<Map<String, Double>> getAllExchangeRates(@RequestParam("from") String fromCurrency) {
        try {
            Map<String, Double> rates = exchangeRateService.getAllExchangeRates(fromCurrency);

            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", Double.NaN));
        }
    }

    @GetMapping("/convert")
    @Operation(
            summary = "Convert a given currency amount to another",
            description = "Takes 'from', 'to', and 'amount' as query parameters and returns the converted value.")
    public ResponseEntity<Double> convertCurrency(@RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        try {
            return ResponseEntity.ok(exchangeRateService.convertCurrency(from, to, amount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Double.NaN);
        }
    }

    @GetMapping("/convert-multiple")
    @Operation(
            summary = "Convert a given currency amount to multiple currencies",
            description = "Takes 'from', 'toCurrencies' (comma-separated), and 'amount' as query parameters and returns the converted values.")
    public ResponseEntity<Map<String, Double>> convertCurrencyToMultiple(@RequestParam String from, @RequestParam String toCurrencies, @RequestParam double amount) {
        try {
            return ResponseEntity.ok(exchangeRateService.convertCurrencyToMultiple(from, toCurrencies, amount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", Double.NaN));
        }
    }
}


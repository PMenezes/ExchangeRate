package com.exchangerates.resolver;

import com.exchangerates.service.ExchangeRateService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExchangeRateQueryResolver {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateQueryResolver(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
    }

    public Double convertCurrency(String fromCurrency, String toCurrency, Double amount) {
        return exchangeRateService.convertCurrency(fromCurrency, toCurrency, amount);
    }

    public List<ExchangeRate> getAllExchangeRates(String fromCurrency) {
        Map<String, Double> rates = exchangeRateService.getAllExchangeRates(fromCurrency);

        // Convert the map of rates to a list of ExchangeRate objects
        return rates.entrySet().stream()
                .map(entry -> new ExchangeRate(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ExchangeRate> convertCurrencyToMultiple(String fromCurrency, String toCurrencies, Double amount) {
        Map<String, Double> conversions = exchangeRateService.convertCurrencyToMultiple(fromCurrency, toCurrencies, amount);

        // Convert the map of conversions to a list of CurrencyConversion objects
        return conversions.entrySet().stream()
                .map(entry -> new ExchangeRate(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}

package com.exchangerates.service;

import com.exchangerates.model.ExchangeRate;
import com.exchangerates.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.restTemplate = new RestTemplate();
    }

    public void fetchAndSaveExchangeRate(String baseCurrency, String targetCurrency) {
        String url = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s", baseCurrency, targetCurrency);

        // Fetch data from the API
        Map response = restTemplate.getForObject(url, Map.class);

        // Extract the exchange rate
        if (response != null && response.containsKey("rates")) {
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            double rate = rates.get(targetCurrency);

            // Save to the database
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrency(baseCurrency);
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setRate(rate);

            exchangeRateRepository.save(exchangeRate);
        }
    }
}

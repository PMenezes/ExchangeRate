package com.exchangerates.service;

import com.exchangerates.dto.ExchangeRateDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final Map<String, CachedRate> rateCache = new ConcurrentHashMap<>();

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
    }


    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        String key = fromCurrency + "_" + toCurrency;

        // Check if the rate is in the cache and not older than 1 minute
        CachedRate cachedRate = rateCache.get(key);
        if (cachedRate != null && cachedRate.timestamp().isAfter(Instant.now().minusSeconds(60))) {
            return cachedRate.rate();
        }

        // Fetch data from the external API
        String url = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s", fromCurrency, toCurrency);
        ExchangeRateDto response = restTemplate.getForObject(url, ExchangeRateDto.class);

        if (response != null && response.getRates() != null) {
            Double rate = response.getRates().get(toCurrency);

            // Cache the rate
            rateCache.put(key, new CachedRate(rate, Instant.now()));
            return rate;
        }

        throw new RuntimeException("Unable to fetch exchange rate");
    }

    @Scheduled(fixedRate = 60000) // Refresh every 60 seconds
    public void refreshPopularRates() {
        String[] popularPairs = {"USD_EUR", "USD_GBP", "EUR_GBP"}; // Example pairs

        for (String pair : popularPairs) {
            String[] currencies = pair.split("_");
            String fromCurrency = currencies[0];
            String toCurrency = currencies[1];

            try {
                getExchangeRate(fromCurrency, toCurrency);
            } catch (Exception ex) {
                // Handle refresh failure (e.g., log the error)
            }
        }
    }
}

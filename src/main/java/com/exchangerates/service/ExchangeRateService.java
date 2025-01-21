package com.exchangerates.service;

import com.exchangerates.dto.ExchangeRateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    @Value("${external.api.url}")
    private String apiUrl;

    @Value("${external.api.accessKey}")
    private String accessKey;

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        return null;
    }

    @Cacheable(value = "exchangeRates", key = "#fromCurrency", unless = "#result == null || #result.getQuotes().isEmpty()")
    public ExchangeRateDto getAllExchangeRates(String fromCurrency) {
        // Build the URL
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/live")
                .queryParam("access_key", accessKey)
                .queryParam("source", fromCurrency)
                .toUriString();

        ExchangeRateDto response = restTemplate.getForObject(url, ExchangeRateDto.class);

        if (response != null && response.getQuotes() != null) {
            return response;
        }

        throw new RuntimeException("Failed to fetch exchange rates");
    }

    @Cacheable(value = "currencyConversions", key = "#fromCurrency + '_' + #toCurrency + '_' + #amount")
    public Double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        // Build the URL
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/convert")
                .queryParam("access_key", accessKey)
                .queryParam("from", fromCurrency)
                .queryParam("to", toCurrency)
                .queryParam("amount", amount)
                .toUriString();

        // Fetch response
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Extract conversion result
        if (response != null && response.containsKey("result")) {
            return (Double) response.get("result");
        }

        throw new RuntimeException("Failed to fetch conversion value");
    }
}

package com.exchangerates.service;

import com.exchangerates.config.ExternalApiProperties;
import com.exchangerates.dto.ExchangeRateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final ExternalApiProperties properties;

    public ExchangeRateService(RestTemplate restTemplate, ExternalApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Cacheable(value = "exchangeRateCache", key = "#fromCurrency", unless = "#result == null")
    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        // Build the exchangerate.host URL
        String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/live")
                .queryParam("access_key", properties.getAccessKey())
                .queryParam("source", fromCurrency)
                .queryParam("currencies", toCurrency)
                .toUriString();

        // Fetch the response
        ExchangeRateDto result = restTemplate.getForObject(url, ExchangeRateDto.class);

        if (result != null && result.getQuotes() != null) {
            return result.getQuotes().get(fromCurrency+toCurrency);
        }

        throw new RuntimeException("Failed to fetch exchange rate for " + fromCurrency + " to " + toCurrency);
    }

    @Cacheable(value = "exchangeRateCache", key = "#fromCurrency + '_ALL'", unless = "#result == null || #result.isEmpty()")
    public Map<String, Double> getAllExchangeRates(String fromCurrency) {
        // Build the exchangerate.host URL
        String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/live")
                .queryParam("access_key", properties.getAccessKey())
                .queryParam("source", fromCurrency)
                .toUriString();

        // Fetch response
        ExchangeRateDto result = restTemplate.getForObject(url, ExchangeRateDto.class);

        // Extract conversion result
        if (result != null && result.getQuotes() != null) {
            return result.getQuotes();
        }

        throw new RuntimeException("Failed to fetch exchange rates");
    }

    @Cacheable(value = "exchangeRateCache", key = "#fromCurrency + '_' + #toCurrency + '_' + #amount")
    public Double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        // Build the exchangerate.host URL
        String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/convert")
                .queryParam("access_key", properties.getAccessKey())
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

    @Cacheable(value = "exchangeRateCache", key = "#fromCurrency + ':' + #toCurrencies + ':' + #amount + '_ALL'")
    public Map<String, Double> convertCurrencyToMultiple(String fromCurrency, String toCurrencies, double amount) {
        String[] targetCurrencies = toCurrencies.split(",");
        Map<String, Double> conversions = new HashMap<>();

        for (String targetCurrency : targetCurrencies) {
            // Build the URL for the /convert endpoint
            String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/convert")
                    .queryParam("access_key", properties.getAccessKey())
                    .queryParam("from", fromCurrency)
                    .queryParam("to", targetCurrency.trim())
                    .queryParam("amount", amount)
                    .toUriString();

            // Fetch the conversion result
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Extract the result and add it to the conversions map
            if (response != null && response.containsKey("result")) {
                Double result = (Double) response.get("result");
                conversions.put(targetCurrency.trim(), result);
            } else {
                throw new RuntimeException("Failed to fetch conversion for " + targetCurrency.trim());
            }
        }

        return conversions;
    }
}

package com.exchangerates;

import com.exchangerates.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Mocks the RestTemplate to simulate external API calls.
 * Verifies the logic in the service method works correctly with mock data.
 */
class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRate() {
        // Arrange
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        String apiUrl = String.format("https://api.exchangerate.host/latest?base=%s&symbols=%s", baseCurrency, targetCurrency);

        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, Double> rates = new HashMap<>();
        rates.put(targetCurrency, 0.85);
        mockResponse.put("rates", rates);

        when(restTemplate.getForObject(apiUrl, Map.class)).thenReturn(mockResponse);

        // Act
        double rate = exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);

        // Assert
        assertEquals(0.85, rate);
    }
}

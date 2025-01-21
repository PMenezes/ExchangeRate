package com.exchangerates;

import com.exchangerates.dto.ExchangeRateDto;
import com.exchangerates.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void testConvertCurrency() {
        // Mock API response
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("result", 85.0); // Example conversion value

        // Mock RestTemplate behavior
        when(restTemplate.getForObject(anyString(), (Class<Map>) any())).thenReturn(mockResponse);

        // Call the method under test
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        double amount = 100.0;

        Double result = exchangeRateService.convertCurrency(fromCurrency, toCurrency, amount);

        // Assert the result
        assertEquals(85.0, result, 0.01, "Conversion value should match the mocked response");
    }


    @Test
    public void testCacheableBehavior() {
        String baseCurrency = "USD";
        String url = "https://api.exchangerate.host/live?access_key=ce648ff03367120f4ee491ea636b8adb&source=USD";

        Map<String, Double> mockRates = Map.of("USDEUR", 0.85, "USDGBP", 0.75);
        Map<String, Object> mockResponse = Map.of("source", "USD", "quotes", mockRates);

        // Mock external API response
        when(restTemplate.getForEntity(url, Map.class))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        // First call - should fetch from API
        ExchangeRateDto rates1 = exchangeRateService.getAllExchangeRates(baseCurrency);
        assertEquals(0.85, rates1.getQuotes().get("USDEUR"));

        // Second call - should fetch from cache
        ExchangeRateDto rates2 = exchangeRateService.getAllExchangeRates(baseCurrency);
        assertEquals(0.85, rates2.getQuotes().get("USDEUR"));

        // Verify external API was only called once
        verify(restTemplate, times(1)).getForEntity(url, Map.class);
    }
}

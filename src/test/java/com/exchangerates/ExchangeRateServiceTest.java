package com.exchangerates;

import com.exchangerates.config.ExternalApiProperties;
import com.exchangerates.dto.ExchangeRateDto;
import com.exchangerates.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Mocks the RestTemplate to simulate external API calls.
 * Verifies the logic in the service method works correctly with mock data.
 */
@SpringBootTest
@EnableConfigurationProperties(ExternalApiProperties.class)
@EnableCaching  // Enable caching in tests
class ExchangeRateServiceTest {

    private ExchangeRateService exchangeRateService;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ExternalApiProperties properties;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        exchangeRateService = new ExchangeRateService(restTemplate, properties);
    }

    @Test
    public void testGetExchangeRate_Success() {
        // Arrange
        String url = "https://api.exchangerate.host/live?access_key=ce648ff03367120f4ee491ea636b8adb&source=USD&currencies=EUR";
        ExchangeRateDto mockRates = new ExchangeRateDto("USD", Map.of("USDEUR", 1.23));

        when(restTemplate.getForObject(eq(url), eq(ExchangeRateDto.class))).thenReturn(mockRates);

        // Act
        Double result = exchangeRateService.getExchangeRate("USD", "EUR");

        // Assert
        assertEquals(1.23, result);

        // Verify the RestTemplate call
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ExchangeRateDto.class));
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
    void testConvertMultipleCurrency() {
        // Mock API response
        Map<String, Object> mockResponse1 = new HashMap<>();
        mockResponse1.put("result", 85.0); // Example conversion value

        Map<String, Object> mockResponse2 = new HashMap<>();
        mockResponse2.put("result", 90.0); // Example conversion value

        String url1 = "https://api.exchangerate.host/convert?access_key=ce648ff03367120f4ee491ea636b8adb&from=USD&to=EUR&amount=100.0";
        String url2 = "https://api.exchangerate.host/convert?access_key=ce648ff03367120f4ee491ea636b8adb&from=USD&to=CAD&amount=100.0";


        // Mock RestTemplate behavior in two different calls
        when(restTemplate.getForObject(url1, Map.class)).thenReturn(mockResponse1);
        when(restTemplate.getForObject(url2, Map.class)).thenReturn(mockResponse2);

        // Call the method under test
        String fromCurrency = "USD";
        String toCurrency = "EUR,CAD";
        double amount = 100.0;

        Map<String, Double> result = exchangeRateService.convertCurrencyToMultiple(fromCurrency, toCurrency, amount);

        // Assert the result
        assertEquals(85.0, result.get("EUR"), 0.01, "Conversion value should match the mocked response");
        assertEquals(90.0, result.get("CAD"), 0.01, "Conversion value should match the mocked response");
    }

    @Test
    public void testCacheableBehavior() {
        String baseCurrency = "USD";
        Map<String, Double> mockRates = Map.of("USDEUR", 0.85, "USDGBP", 0.75);

        // Mock external API response
        ExchangeRateDto mockExchangeRates = new ExchangeRateDto("USD", mockRates);
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateDto.class))).thenReturn(mockExchangeRates);

        // First call - should fetch from API
        Map<String, Double> rates1 = exchangeRateService.getAllExchangeRates(baseCurrency);
        assertEquals(0.85, rates1.get("USDEUR"));

        // Check if cache has been populated
        Cache cache = cacheManager.getCache("exchangeRateCache");
        assertNotNull(cache);
        assertNotNull(cache.get(baseCurrency + "_ALL"));  // Check that cache contains the value for the key "USD"

        // Second call - should fetch from cache
        Map<String, Double> rates2 = exchangeRateService.getAllExchangeRates(baseCurrency);
        assertEquals(0.85, rates2.get("USDEUR"));

        // Verify external API was only called once
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ExchangeRateDto.class));

        // Check if the cache is still holding the value
        assertNotNull(cache.get(baseCurrency));  // Confirm cache has the key "USD"
    }
}

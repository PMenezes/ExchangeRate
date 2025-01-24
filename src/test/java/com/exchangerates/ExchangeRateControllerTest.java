package com.exchangerates;

import com.exchangerates.filter.ExchangeRateLimitingFilter;
import com.exchangerates.service.ExchangeRateService;
import com.exchangerates.controller.ExchangeRateController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Uses MockMvc to simulate HTTP requests to the REST API.
 * Mocks the service layer to isolate the controller's functionality.
 */
@WebMvcTest(controllers = ExchangeRateController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ExchangeRateLimitingFilter.class})
})
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private CacheManager cacheManager;

    @InjectMocks
    private ExchangeRateController exchangeRateController;


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetExchangeRate_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(1.23);

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/rate")
                        .param("from", fromCurrency)
                        .param("to", toCurrency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1.23));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getExchangeRate(fromCurrency, toCurrency);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetExchangeRate_InternalServerError() throws Exception {
        // Arrange: Mock the service to throw an exception
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenThrow(new RuntimeException("Error"));

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/rate")
                        .param("from", fromCurrency)
                        .param("to", toCurrency))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value(Double.NaN));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getExchangeRate(fromCurrency, toCurrency);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllExchangeRates_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        Map<String, Double> mockQuotes = Map.of("USDEUR", 1.23, "USDGBP", 0.75);

        when(exchangeRateService.getAllExchangeRates(fromCurrency)).thenReturn(mockQuotes);

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/all")
                        .param("from", fromCurrency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.USDEUR").value(1.23))
                .andExpect(jsonPath("$.USDGBP").value(0.75));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getAllExchangeRates(fromCurrency);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetAllExchangeRates_InternalServerError() throws Exception {
        // Arrange: Mock the service to throw an exception
        String fromCurrency = "USD";
        when(exchangeRateService.getAllExchangeRates(fromCurrency)).thenThrow(new RuntimeException("Error"));

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/all")
                        .param("from", fromCurrency))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(Double.NaN));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getAllExchangeRates(fromCurrency);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testConvertCurrency_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        double amount = 100.0;
        double convertedAmount = 85.0;  // Mocked converted value

        when(exchangeRateService.convertCurrency(fromCurrency, toCurrency, amount)).thenReturn(convertedAmount);

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/convert")
                        .param("from", fromCurrency)
                        .param("to", toCurrency)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(convertedAmount));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).convertCurrency(fromCurrency, toCurrency, amount);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testConvertCurrency_InternalServerError() throws Exception {
        // Arrange: Mock the service to throw an exception
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        double amount = 100.0;
        when(exchangeRateService.convertCurrency(fromCurrency, toCurrency, amount)).thenThrow(new RuntimeException("Error"));

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/convert")
                        .param("from", fromCurrency)
                        .param("to", toCurrency)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value(Double.NaN));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).convertCurrency(fromCurrency, toCurrency, amount);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testConvertCurrencyToMultiple_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        String toCurrencies = "EUR,GBP,JPY";
        double amount = 100.0;

        Map<String, Double> conversionResults = Map.of(
                "EUR", 85.0,
                "GBP", 75.0,
                "JPY", 11000.0
        );

        when(exchangeRateService.convertCurrencyToMultiple(fromCurrency, toCurrencies, amount)).thenReturn(conversionResults);

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/convert-multiple")
                        .param("from", fromCurrency)
                        .param("toCurrencies", toCurrencies)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EUR").value(85.0))
                .andExpect(jsonPath("$.GBP").value(75.0))
                .andExpect(jsonPath("$.JPY").value(11000.0));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).convertCurrencyToMultiple(fromCurrency, toCurrencies, amount);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testConvertCurrencyToMultiple_InternalServerError() throws Exception {
        // Arrange: Mock the service to throw an exception
        String fromCurrency = "USD";
        String toCurrencies = "EUR,GBP,JPY";
        double amount = 100.0;
        when(exchangeRateService.convertCurrencyToMultiple(fromCurrency, toCurrencies, amount)).thenThrow(new RuntimeException("Error"));

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/convert-multiple")
                        .param("from", fromCurrency)
                        .param("toCurrencies", toCurrencies)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(Double.NaN));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).convertCurrencyToMultiple(fromCurrency, toCurrencies, amount);
    }
}

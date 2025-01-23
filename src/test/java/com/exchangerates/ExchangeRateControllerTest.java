package com.exchangerates;

import com.exchangerates.dto.ExchangeRateDto;
import com.exchangerates.service.ExchangeRateService;
import com.exchangerates.controller.ExchangeRateController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Uses MockMvc to simulate HTTP requests to the REST API.
 * Mocks the service layer to isolate the controller's functionality.
 */
@SpringBootTest(classes = ExchangeRatesApiApplication.class)
@AutoConfigureMockMvc //need this in Spring Boot test
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController).build();
    }

    @Test
    public void testGetExchangeRate_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        ExchangeRateDto mockRateDto = mock(ExchangeRateDto.class);
        when(exchangeRateService.getExchangeRate(fromCurrency, toCurrency)).thenReturn(mockRateDto);
        when(mockRateDto.getQuotes()).thenReturn(Map.of("USDEUR", 1.23));

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
    public void testGetAllExchangeRates_Success() throws Exception {
        // Arrange: Mock the service response
        String fromCurrency = "USD";
        ExchangeRateDto mockRateDto = mock(ExchangeRateDto.class);
        Map<String, Double> mockQuotes = Map.of("USDEUR", 1.23, "USDGBP", 0.75);
        when(exchangeRateService.getAllExchangeRates(fromCurrency)).thenReturn(mockRateDto);
        when(mockRateDto.getSource()).thenReturn("USD");
        when(mockRateDto.getQuotes()).thenReturn(mockQuotes);

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/all")
                        .param("from", fromCurrency))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.rates.USDEUR").value(1.23))
                .andExpect(jsonPath("$.rates.USDGBP").value(0.75));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getAllExchangeRates(fromCurrency);
    }

    @Test
    public void testGetAllExchangeRates_InternalServerError() throws Exception {
        // Arrange: Mock the service to throw an exception
        String fromCurrency = "USD";
        when(exchangeRateService.getAllExchangeRates(fromCurrency)).thenThrow(new RuntimeException("Error"));

        // Act & Assert: Perform the request and verify the response
        mockMvc.perform(get("/api/exchange-rate/all")
                        .param("from", fromCurrency))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error"));

        // Verify that the service was called
        verify(exchangeRateService, times(1)).getAllExchangeRates(fromCurrency);
    }

    @Test
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

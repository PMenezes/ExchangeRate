package com.exchangerates;

import com.exchangerates.service.ExchangeRateService;
import com.exchangerates.controller.ExchangeRateController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Uses MockMvc to simulate HTTP requests to the REST API.
 * Mocks the service layer to isolate the controller's functionality.
 */
@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRate() throws Exception {
        // Arrange
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        double expectedRate = 0.85;

        when(exchangeRateService.getExchangeRate(baseCurrency, targetCurrency)).thenReturn(expectedRate);

        // Act & Assert
        mockMvc.perform(get("/api/v1/exchange-rates")
                        .param("from", baseCurrency)
                        .param("to", targetCurrency))
                .andExpect(status().isOk())
                .andExpect(content().string("0.85"));
    }
}

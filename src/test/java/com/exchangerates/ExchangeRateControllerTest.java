package com.exchangerates;

import com.exchangerates.service.ExchangeRateService;
import com.exchangerates.controller.ExchangeRateController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
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
    void testConvertCurrencyEndpoint() throws Exception {
        // Mock the service response
        Mockito.when(exchangeRateService.convertCurrency(anyString(), anyString(), anyDouble()))
                .thenReturn(85.0);

        // Perform GET request
        mockMvc.perform(get("/convert")
                        .param("from", "USD")
                        .param("to", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("85.0"));

        // Verify service interaction
        Mockito.verify(exchangeRateService)
                .convertCurrency("USD", "EUR", 100.0);
    }
}

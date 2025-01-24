package com.exchangerates;

import com.exchangerates.config.RateLimiter;
import com.exchangerates.controller.ExchangeRateController;
import com.exchangerates.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ExchangeRateController.class)
public class RateLimitingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private RateLimiter rateLimiter;  // Mock the rate limiter

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testRateLimitNotExceeded() throws Exception {
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        // Simulate that the rate limiter allows requests
        when(rateLimiter.isAllowed(anyString())).thenReturn(true);

        // Make an API request
        mockMvc.perform(get("/api/exchange-rate/rate")
                        .param("from", fromCurrency)
                        .param("to", toCurrency))
                .andExpect(status().isOk()); // Expect OK response if rate limit not exceeded
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testRateLimitExceeded() throws Exception {
        String fromCurrency = "USD";
        String toCurrency = "EUR";

        // Simulate that the rate limiter blocks requests
        when(rateLimiter.isAllowed(anyString())).thenReturn(false);

        // Make an API request that exceeds rate limit
        mockMvc.perform(get("/api/exchange-rate/rate")
                        .param("from", fromCurrency)
                        .param("to", toCurrency))
                .andExpect(status().isTooManyRequests());  // Expect 429 status code
    }
}

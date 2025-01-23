package com.exchangerates.config;

import com.exchangerates.filter.ExchangeRateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ExchangeRateLimitingFilter> rateLimitingFilter(ExchangeRateLimitingFilter exchangeRateLimitingFilter) {
        FilterRegistrationBean<ExchangeRateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(exchangeRateLimitingFilter);
        registrationBean.addUrlPatterns("/api/exchange-rate/*"); // Apply to specific paths
        return registrationBean;
    }
}

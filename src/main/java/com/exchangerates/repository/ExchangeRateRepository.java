package com.exchangerates.repository;

import com.exchangerates.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    ExchangeRate findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);
}

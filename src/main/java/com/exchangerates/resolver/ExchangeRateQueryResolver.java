package com.exchangerates.resolver;

//import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateQueryResolver {


    public ExchangeRateQueryResolver() {
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        return null;
    }
}

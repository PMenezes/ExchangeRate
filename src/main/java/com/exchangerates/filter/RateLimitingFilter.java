package com.exchangerates.filter;

import com.exchangerates.config.RateLimiter;
import jakarta.servlet.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RateLimitingFilter implements Filter {

    private final RateLimiter rateLimiter;

    public RateLimitingFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientId = getClientId(httpRequest);
        if (!rateLimiter.isAllowed(clientId)) {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        // Use a unique identifier for each client, e.g., an API key or IP address
        return request.getRemoteAddr();
    }
}

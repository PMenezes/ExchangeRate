package com.exchangerates.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final long capacity; // Maximum requests allowed
    private final long refillTimeMs; // Refill every 1 minute
    private final Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    public RateLimiter(RateLimiterProperties properties) {
        this.capacity = properties.getCapacity();
        this.refillTimeMs = properties.getRefillTimeMs();
    }

    public boolean isAllowed(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket(capacity, refillTimeMs));
        return bucket.tryConsume();
    }

    private static class TokenBucket {
        private final long capacity;
        private final long refillTimeMs;
        private long tokens;
        private long lastRefillTimestamp;

        public TokenBucket(long capacity, long refillTimeMs) {
            this.capacity = capacity;
            this.refillTimeMs = refillTimeMs;
            this.tokens = capacity;
            this.lastRefillTimestamp = Instant.now().toEpochMilli();
        }

        synchronized boolean tryConsume() {
            refillTokens();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refillTokens() {
            long now = Instant.now().toEpochMilli();
            if (now > lastRefillTimestamp + refillTimeMs) {
                long refillAmount = (now - lastRefillTimestamp) / refillTimeMs * capacity;
                tokens = Math.min(capacity, tokens + refillAmount);
                lastRefillTimestamp = now;
            }
        }
    }
}

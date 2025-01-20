package com.exchangerates.service;

import java.time.Instant;

// Helper class to store rate and timestamp
public record CachedRate(Double rate, Instant timestamp) {}

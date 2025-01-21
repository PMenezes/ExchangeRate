package com.exchangerates.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @DeleteMapping("/cache/clear")
    @Operation(summary = "Manually clears a specific cache")
    public void clearCache(@RequestParam String name) {
        Objects.requireNonNull(cacheManager.getCache(name)).clear();
    }
}

package com.topglobanksoft.statistics_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up caching for the application.
 * Uses settings from application.properties by default.
 */
@Configuration
@EnableCaching // Turns on caching features
public class CacheConfig {
    // Can add special cache rules here if needed later
}
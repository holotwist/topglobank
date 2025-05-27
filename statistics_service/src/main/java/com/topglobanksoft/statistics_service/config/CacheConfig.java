package com.topglobanksoft.statistics_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching // Enable Spring cache support
public class CacheConfig {
    // The basic configuration is taken from application.properties
    // Custom CacheManager beans can be added if more control is needed.
}
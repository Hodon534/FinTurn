package com.webapp.FinTurn.configuration;

import static com.webapp.FinTurn.constant.CorsConstant.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        corsConfiguration.setAllowCredentials(ALLOW_CREDENTIALS);
        corsConfiguration.setAllowedOrigins(Collections.singletonList(ALLOWED_ORIGIN));
        corsConfiguration.setAllowedHeaders(Arrays.asList(ALLOWED_HEADERS_LIST));
        corsConfiguration.setExposedHeaders(Arrays.asList(EXPOSED_HEADERS_LIST));
        corsConfiguration.setAllowedMethods(Arrays.asList(ALLOWED_METHODS_LIST));
        urlBasedCorsConfigurationSource.registerCorsConfiguration(PATTERN_ALL, corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}

package com.team01.billage.config;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PermitAllUrlConfig {

    public List<String> getPermitAllUrls() {
        return List.of(
            "/css/**",
            "/images/**",
            "/js/**",
            "/favicon.ico",
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/**",
            "/connect/**"
        );
    }
}

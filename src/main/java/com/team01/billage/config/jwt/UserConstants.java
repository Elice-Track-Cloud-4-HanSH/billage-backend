package com.team01.billage.config.jwt;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class UserConstants {
    public static final String REFRESH_TOKEN_TYPE_VALUE = "refreshToken";
    public static final String ACCESS_TOKEN_TYPE_VALUE = "accessToken";
    public static final String NOT_FOUND_REFRESH_TOKEN = "notFoundRefreshToken";

    @Value("${REFRESH_TOKEN_DURATION}")
    private long refreshTokenDuration;

    @Value("${ACCESS_TOKEN_DURATION}")
    private long accessTokenDuration;

    public Duration getRefreshTokenDuration() {
        return Duration.ofDays(refreshTokenDuration);
    }

    public Duration getAccessTokenDuration() {
        return Duration.ofMinutes(accessTokenDuration);
    }
}
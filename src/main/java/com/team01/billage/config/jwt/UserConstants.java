package com.team01.billage.config.jwt;

import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor
public class UserConstants {
    public static final String REFRESH_TOKEN_TYPE_VALUE = "refreshToken"; //리프레시 토큰 이름
    public static final String ACCESS_TOKEN_TYPE_VALUE = "accessToken"; //엑세스토큰이름
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);  //리프레시토큰 유호기간
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15); // 액세스 토큰 유효 기간
}
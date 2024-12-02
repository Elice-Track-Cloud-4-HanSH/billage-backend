package com.team01.billage.config.jwt;

public record JwtDto(
        String accessToken,
        String refreshToken) {

}
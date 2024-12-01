package com.team01.billage.config.jwt;

import com.team01.billage.user.domain.UserRole;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface JwtProvider <T>{
    T convertAuthToken(String token);
    Authentication getAuthentication(T authToken);
    T createAccessToken(String userId, UserRole role, Map<String, Object> claims);
    T createRefreshToken(String userId, UserRole role, Map<String, Object> claims);
}

package com.team01.billage.user.service;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.user.repository.TokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private final TokenRedisRepository tokenRedisRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        // Redis에서 refresh token 삭제
        String accessToken = CookieUtil.getCookieValue(request, "accessToken");

        if (accessToken != null) {
            // Redis에서 해당 유저의 토큰 정보 삭제
            tokenRedisRepository.deleteById(accessToken);
        }

        // 쿠키 삭제
        CookieUtil.deleteCookie(request, response, "accessToken");
        CookieUtil.deleteCookie(request, response, "refreshToken");
    }
}
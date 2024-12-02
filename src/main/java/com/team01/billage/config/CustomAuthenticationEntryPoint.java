package com.team01.billage.config;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // HTTP 상태 코드와 메시지 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 에러 응답 생성 및 전송
        String jsonResponse = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}";
        response.getWriter().write(jsonResponse);
    }
}

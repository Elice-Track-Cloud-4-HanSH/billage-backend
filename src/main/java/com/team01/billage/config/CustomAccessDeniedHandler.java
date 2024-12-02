package com.team01.billage.config;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = "{\"message\":\"접근 권한이 없습니다.\",\"code\":\"ACCESS_DENIED\"}";
        response.getWriter().write(jsonResponse);
    }
}
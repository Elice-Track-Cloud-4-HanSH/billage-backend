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
            "/api/categories",
            "/api/products/**", // 나중에 GET 메서드만 허용으로 변경
            "/api/chatroom/**",
            "/connect/**" ,  // SockJs 연결을 위한 엔드포인트
                "/**",
                "/api/users/after-login"
        );
    }
}

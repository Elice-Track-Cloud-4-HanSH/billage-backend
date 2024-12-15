package com.team01.billage.config.jwt;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import jakarta.servlet.*;
        import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.team01.billage.config.jwt.UserConstants.*;

/**
 * 서버에 요청이 들어올때 요청 데이터를 SecurityContext 가
 * 인터셉터해서 FilterChain 에 정의에 의해
 * jwtFilter 를 무조건 거치게 됨.
 * */

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl tokenProvider;
    private final UserConstants userConstants;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

            String jwtAccessToken = resolveAccessTokenFromCookies(request);

            if (jwtAccessToken != null && tokenProvider.validateToken(jwtAccessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(jwtAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }


    // 쿠키에서 accessToken 을 추출
    private String resolveAccessTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        // 쿠키에서 "accessToken" 추출
        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> ACCESS_TOKEN_TYPE_VALUE.equals(cookie.getName()))
                .findFirst();

        return jwtCookie.map(Cookie::getValue).orElse(null);
    }
}

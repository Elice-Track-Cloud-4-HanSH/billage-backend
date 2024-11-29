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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // JWT 토큰을 쿠키에서 추출
        String jwtAccessToken = resolveAccessTokenFromCookies(request);

        //로그인 api 거나 액세스토큰을 가지고있지 않다면 바로 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/login") || jwtAccessToken==null) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean validateToken = tokenProvider.validateToken(jwtAccessToken);
        if (validateToken) {
            // 액세스토큰이 유효한 경우, Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(jwtAccessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // 액세스토큰이 만료된 경우, 리프레시토큰 검증
            log.info("Access token expired, validating refresh token...");

            // 리프레시토큰이 유효하다면 새로운 액세스토큰 발급
            // 해당 메소드에서 유효성 검사까지 같이 진행
//            String newAccessToken = tokenProvider.generateAccessTokenFromRefreshToken(jwtRefreshToken);

            //레디스에 있는 리프레시토큰을 가져와 새로운 엑세스토큰 발급
            String newAccessToken = tokenProvider.generateAccessTokenFromRefreshTokenByRedis(jwtAccessToken);

            //db에 리프레시토큰이 (null)없다면  자동 로그아웃
            if (newAccessToken.equals(NOT_FOUND_REFRESH_TOKEN)) {
                //쿠키삭제(로그아웃)
                CookieUtil.deleteTokenCookie(response,ACCESS_TOKEN_TYPE_VALUE);
                filterChain.doFilter(request, response);
                return;
            }

            log.info("액세스토큰 발급 완료..!");


            int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds(); // 쿠키 유효 기간 설정

            //쿠키 생성
            CookieUtil.addCookie(response,ACCESS_TOKEN_TYPE_VALUE,newAccessToken,cookieMaxAge);

            // 새로운 액세스토큰으로 Authentication 객체 생성
            Authentication authentication = tokenProvider.getAuthentication(newAccessToken);
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

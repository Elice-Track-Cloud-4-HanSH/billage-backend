package com.team01.billage.user.service;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.exception.CustomException;
import com.team01.billage.user.dto.JwtTokenDto;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.dto.Response.JwtTokenResponse;
import com.team01.billage.user.dto.Response.UserValidateTokenResponseDto;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_DURATION;
import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;

// AuthenticationFacade.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFacade {
    private final TokenService tokenService;
    private final UserService userService;
    private final AuthResponseBuilder responseBuilder;

    public ResponseEntity<JwtTokenResponse> handleLogin(
            JwtTokenLoginRequest request,
            HttpServletResponse response,
            Cookie existingAccessTokenCookie
    ) {
        try {
            // 기본 검증
            if (!userService.validateLoginRequest(request)) {
                return responseBuilder.buildLoginFailResponse("아이디와 비밀번호를 입력하세요");
            }

            // 기존 토큰 처리
            handleExistingToken(response, existingAccessTokenCookie);

            // 로그인 처리 및 새 토큰 발급
            JwtTokenDto tokenDto = tokenService.login(request);

            // 쿠키 설정
            setTokenCookie(response, tokenDto.getAccessToken());

            return responseBuilder.buildLoginSuccessResponse(tokenDto);

        } catch (CustomException e) {
            log.info("Login failed: {}", e.getMessage());
            return responseBuilder.buildErrorResponse(e);
        }
    }

    public ResponseEntity<UserValidateTokenResponseDto> validateProtectedResource(String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            return responseBuilder.buildNotLoggedInResponse();
        }

        boolean isValid = tokenService.validateToken(accessToken);
        return isValid ?
                responseBuilder.buildTokenValidResponse() :
                responseBuilder.buildTokenInvalidResponse();
    }

    private void handleExistingToken(HttpServletResponse response, Cookie existingAccessTokenCookie) {
        if (existingAccessTokenCookie != null) {
            CookieUtil.deleteTokenCookie(response, ACCESS_TOKEN_TYPE_VALUE);
        }
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        CookieUtil.addCookie(
                response,
                ACCESS_TOKEN_TYPE_VALUE,
                token,
                (int) ACCESS_TOKEN_DURATION.toSeconds()
        );
    }
}
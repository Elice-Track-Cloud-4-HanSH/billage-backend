package com.team01.billage.user.controller;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.dto.JwtTokenDto;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.dto.Response.JwtTokenResponse;
import com.team01.billage.user.dto.Response.UserValidateTokenResponseDto;
import com.team01.billage.user.service.TokenService;
import com.team01.billage.user.service.UserService;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_DURATION;
import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TokenApiController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(
            @RequestBody JwtTokenLoginRequest request,
            HttpServletResponse response,
            @CookieValue(value = "accessToken", required = false) Cookie existingAccessTokenCookie
    ) {
        if (!userService.validateLoginRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(JwtTokenResponse.builder()
                            .message("아이디와 비밀번호를 입력하세요")
                            .build());
        }

        if (existingAccessTokenCookie != null) {
            CookieUtil.deleteTokenCookie(response, ACCESS_TOKEN_TYPE_VALUE);
        }

        try {
            JwtTokenDto jwtTokenResponse = tokenService.login(request);

            CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE,
                    jwtTokenResponse.getAccessToken(),
                    (int) ACCESS_TOKEN_DURATION.toSeconds());

            return ResponseEntity.ok()
                    .body(JwtTokenResponse.builder()
                            .accessToken(jwtTokenResponse.getAccessToken())
                            .isAdmin(jwtTokenResponse.getRole().equals(UserRole.ADMIN))
                            .message("로그인 성공")
                            .build());

        } catch (CustomException e) {
            log.info("Login failed: {}", e.getMessage());

            return ResponseEntity
                    .status(e.getErrorCode().getHttpStatus())
                    .body(JwtTokenResponse.builder()
                            .message(e.getErrorCode().getMessage())
                            .build());
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<UserValidateTokenResponseDto> getProtectedResource(
            @CookieValue(value = "accessToken", required = false, defaultValue = "") String accessToken
    ) {
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.ok()
                    .body(UserValidateTokenResponseDto.builder()
                            .message("not login")
                            .build());
        }

        if (tokenService.validateToken(accessToken)) {
            return ResponseEntity.ok()
                    .body(UserValidateTokenResponseDto.builder()
                            .message("success")
                            .build());
        }

        return ResponseEntity.status(ErrorCode.INVALID_TOKEN.getHttpStatus())
                .body(UserValidateTokenResponseDto.builder()
                        .message("fail")
                        .build());
    }
}
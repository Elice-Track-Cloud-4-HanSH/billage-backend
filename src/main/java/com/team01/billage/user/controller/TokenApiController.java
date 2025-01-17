package com.team01.billage.user.controller;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.dto.JwtTokenDto;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.dto.Response.JwtTokenResponse;
import com.team01.billage.user.dto.Response.UserValidateTokenResponseDto;
import com.team01.billage.user.service.AuthenticationFacade;
import com.team01.billage.user.service.TokenService;
import com.team01.billage.user.service.UserService;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team01.billage.config.jwt.UserConstants;


// TokenApiController.java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TokenApiController {
    private final AuthenticationFacade authenticationFacade;
    private final UserConstants userConstants;

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(
            @RequestBody JwtTokenLoginRequest request,
            HttpServletResponse response
    ) {
        return authenticationFacade.handleLogin(request, response);
    }
    @GetMapping("/check")
    public ResponseEntity<UserValidateTokenResponseDto> validateToken(
            @CookieValue(value = "accessToken", required = false, defaultValue = "") String accessToken
    ) {
        return authenticationFacade.validateProtectedResource(accessToken);
    }

}

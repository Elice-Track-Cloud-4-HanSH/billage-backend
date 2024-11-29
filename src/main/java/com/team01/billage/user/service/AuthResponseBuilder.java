package com.team01.billage.user.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.dto.JwtTokenDto;
import com.team01.billage.user.dto.Response.JwtTokenResponse;
import com.team01.billage.user.dto.Response.UserValidateTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

// AuthResponseBuilder.java
@Component
@RequiredArgsConstructor
public class AuthResponseBuilder {

    public ResponseEntity<JwtTokenResponse> buildLoginSuccessResponse(JwtTokenDto tokenDto) {
        return ResponseEntity.ok()
                .body(JwtTokenResponse.builder()
                        .accessToken(tokenDto.getAccessToken())
                        .role(tokenDto.getRole())
                        .message("로그인 성공")
                        .build());
    }

    public ResponseEntity<JwtTokenResponse> buildLoginFailResponse(String message) {
        return ResponseEntity.badRequest()
                .body(JwtTokenResponse.builder()
                        .message(message)
                        .build());
    }

    public ResponseEntity<JwtTokenResponse> buildErrorResponse(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(JwtTokenResponse.builder()
                        .message(e.getErrorCode().getMessage())
                        .build());
    }

    public ResponseEntity<UserValidateTokenResponseDto> buildNotLoggedInResponse() {
        return ResponseEntity.ok()
                .body(UserValidateTokenResponseDto.builder()
                        .message("not login")
                        .build());
    }

    public ResponseEntity<UserValidateTokenResponseDto> buildTokenValidResponse() {
        return ResponseEntity.ok()
                .body(UserValidateTokenResponseDto.builder()
                        .message("success")
                        .build());
    }

    public ResponseEntity<UserValidateTokenResponseDto> buildTokenInvalidResponse() {
        return ResponseEntity.status(ErrorCode.INVALID_TOKEN.getHttpStatus())
                .body(UserValidateTokenResponseDto.builder()
                        .message("fail")
                        .build());
    }
}
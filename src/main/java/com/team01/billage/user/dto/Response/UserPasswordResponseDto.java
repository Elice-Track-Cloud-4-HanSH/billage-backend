package com.team01.billage.user.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

// UserPasswordResponseDto.java
@Schema(description = "비밀번호 확인 응답")
public record UserPasswordResponseDto(
        @Schema(description = "비밀번호 일치 여부")
        boolean matches,
        @Schema(description = "응답 메시지")
        String message
) {}
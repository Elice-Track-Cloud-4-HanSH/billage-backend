package com.team01.billage.user.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 사용 가능 여부 응답")
public record NicknameAvailabilityResponse(
        @Schema(description = "응답 메시지")
        String message
) {
}

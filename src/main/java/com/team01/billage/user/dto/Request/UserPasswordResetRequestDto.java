package com.team01.billage.user.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 확인 요청")
public record UserPasswordResetRequestDto(

        @Schema(description = "비밀번호")
        @NotBlank(message = "비밀번호는 필수입니다")
        String email,

        @Schema(description = "비밀번호")
        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {}

package com.team01.billage.user.dto.Response;

import com.team01.billage.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserRole role;
    private String message;
}

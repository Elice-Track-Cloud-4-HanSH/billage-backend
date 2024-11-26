package com.team01.billage.user.dto.Response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isAdmin;
    private String message;
}

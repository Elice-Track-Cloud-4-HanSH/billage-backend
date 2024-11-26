package com.team01.billage.user.dto;

import com.team01.billage.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtTokenDto {
    private String accessToken;
    private UserRole role;
}

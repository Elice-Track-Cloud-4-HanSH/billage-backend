package com.team01.billage.user.dto.Response;

import com.team01.billage.user.domain.CustomUserDetails;
import lombok.Getter;

@Getter
public class SimpleUserInfoResponseDto {
    private Long userId;
    private String email;

    public SimpleUserInfoResponseDto(CustomUserDetails userDetails) {
        this.userId = userDetails.getId();
        this.email = userDetails.getEmail();
    }
}

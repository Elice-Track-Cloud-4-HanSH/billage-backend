package com.team01.billage.user.dto.Response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCheckIdResponseDto {
    private boolean isAvailable;
    private String message;
}
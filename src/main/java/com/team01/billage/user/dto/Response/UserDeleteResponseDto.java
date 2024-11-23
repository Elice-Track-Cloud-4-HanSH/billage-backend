package com.team01.billage.user.dto.Response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDeleteResponseDto {
    private String message;
    private boolean isDeleted;
}

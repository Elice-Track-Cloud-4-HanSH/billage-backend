package com.team01.billage.user.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenLoginRequest {
    private String userRealId;
    private String password;
}

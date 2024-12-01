package com.team01.billage.user.dto.Request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationRequest {
    private String email;
    private String code;
}
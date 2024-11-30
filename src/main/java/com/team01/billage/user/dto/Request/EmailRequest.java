package com.team01.billage.user.dto.Request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // protected 기본 생성자 추가
@AllArgsConstructor
public class EmailRequest {
    private String email;
}

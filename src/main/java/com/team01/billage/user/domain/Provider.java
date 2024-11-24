package com.team01.billage.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {
    NONE("NONE"), // 일반 구현
    GOOGLE("GOOGLE");   //oauth google 연동 구현
    private final String provider;
}

package com.team01.billage.map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAreaRequestDto {
    private Long emdCd; // 행정구역 코드
    private int depth;
}

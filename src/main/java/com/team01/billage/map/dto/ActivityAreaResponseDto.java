package com.team01.billage.map.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityAreaResponseDto {
    private Long emdCd;       // 행정구역 코드
    private String emdNm;     // 행정구역 이름
    private String sidoNm;  // 시도명 추가
    private String sggNm;  // 시군구명 추가
}

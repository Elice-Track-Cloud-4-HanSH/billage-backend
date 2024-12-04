package com.team01.billage.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmdAreaResponseDto {
    private Long id;       // EMD_CD
    private String sidoNm; // 시도명
    private String sggNm;  // 시군구명
    private String emdNm;  // 읍면동명
}

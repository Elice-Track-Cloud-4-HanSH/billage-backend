package com.team01.billage.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ActivityAreaResponseDto {
    private Long emdCd;       // 행정구역 코드
    private String emdNm;     // 행정구역 이름
    private Long colAdmSe;    // 행정구역의 세부 코드
    private Long sggOid;      // 시군구 OID
}

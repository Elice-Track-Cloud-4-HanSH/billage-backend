package com.team01.billage.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmdAreaSearchRequestDto {
    private String sggNm;  // 시군구명
    private String emdNm;  // 읍면동명
}

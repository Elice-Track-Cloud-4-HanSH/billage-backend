package com.team01.billage.map.dto;

import lombok.Getter;

@Getter
public class EmdAreaGeoJsonResponseDto {

    private String geomGeoJson;  // GeoJSON 데이터

    // Object[]에서 geomGeoJson만 추출
    public EmdAreaGeoJsonResponseDto(Object[] result) {
        this.geomGeoJson = (String) result[0]; // Index를 확인해 필요에 맞게 설정
    }
}

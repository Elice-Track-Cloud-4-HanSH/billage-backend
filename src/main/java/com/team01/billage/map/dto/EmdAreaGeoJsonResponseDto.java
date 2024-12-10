package com.team01.billage.map.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Getter;

@Getter
public class EmdAreaGeoJsonResponseDto {

    private Map<String, Object> geomGeoJson; // JSON 객체로 변경

    // Object[]에서 geomGeoJson 추출 및 파싱
    public EmdAreaGeoJsonResponseDto(Object[] result) {
        try {
            String jsonString = (String) result[0]; // 문자열로 추출
            ObjectMapper objectMapper = new ObjectMapper();
            this.geomGeoJson = objectMapper.readValue(jsonString, Map.class); // JSON 객체로 변환
        } catch (Exception e) {
            throw new RuntimeException("GeoJSON 데이터를 파싱하는 중 오류가 발생했습니다.", e);
        }
    }
}

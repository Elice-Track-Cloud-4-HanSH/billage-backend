package com.team01.billage.map.dto;

import com.team01.billage.map.domain.EmdArea;
import lombok.Getter;
import org.locationtech.jts.geom.MultiPolygon;

@Getter
public class EmdAreaGeoResponseDto {

    private Long emdCd;          // 읍면동 코드
    private String sidoNm;       // 시도명
    private String sggNm;        // 시군구명
    private String emdNm;        // 읍면동명
    private MultiPolygon geom;         //

    // 엔티티를 DTO로 변환하는 생성자
    public EmdAreaGeoResponseDto(EmdArea emdArea) {
        this.emdCd = emdArea.getId();
        this.sidoNm = emdArea.getSidoNm();
        this.sggNm = emdArea.getSggNm();
        this.emdNm = emdArea.getEmdNm();
        this.geom = emdArea.getGeom();
    }
}

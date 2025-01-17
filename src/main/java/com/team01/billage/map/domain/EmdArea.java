package com.team01.billage.map.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "emd_area")  // 테이블 이름 매핑
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class EmdArea {

    @Id
    @Column(name = "emd_cd", length = 20, nullable = false)
    private Long id;  // PRIMARY KEY로 사용

    @Column(name = "col_adm_se", length = 20)
    private Long colAdmSe;

    @Column(name = "sido_nm", length = 50)
    private String sidoNm;  // 시도명 추가

    @Column(name = "sgg_nm", length = 50)
    private String sggNm;  // 시군구명 추가

    @Column(name = "emd_nm", length = 255)
    private String emdNm;

    @Column(name = "sgg_oid")
    private Long sggOid;

    @Column(columnDefinition = "geometry", nullable = false)
    private MultiPolygon geom;
}
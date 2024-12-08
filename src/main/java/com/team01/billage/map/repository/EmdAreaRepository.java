package com.team01.billage.map.repository;

import com.team01.billage.map.domain.EmdArea;
import com.team01.billage.map.dto.EmdAreaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmdAreaRepository extends JpaRepository<EmdArea, Long> {

    @Query("""
        SELECT new com.team01.billage.map.dto.EmdAreaResponseDto(
            e.id, e.sidoNm, e.sggNm, e.emdNm
        )
        FROM EmdArea e
        WHERE (:sggNm IS NULL OR e.sggNm LIKE %:sggNm%)
          AND (:emdNm IS NULL OR e.emdNm LIKE %:emdNm%)
    """)
    Page<EmdAreaResponseDto> searchEmdAreas(String sggNm, String emdNm, Pageable pageable);

    @Query(value = """
    SELECT ST_AsGeoJSON(geom) AS geomGeoJson
    FROM emd_area
    WHERE emd_cd = :emdCd
""", nativeQuery = true)
    Object findEmdAreaWithGeoJsonById(@Param("emdCd") Long emdCd);

}


package com.team01.billage.map.repository;

import com.team01.billage.map.domain.NeighborArea;
import com.team01.billage.map.domain.EmdArea;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeighborAreaRepository extends JpaRepository<NeighborArea, Long> {
    // 특정 행정동 코드로 주변 지역 검색
    List<NeighborArea> findByEmdArea(EmdArea emdArea);
    Optional<NeighborArea> findByEmdAreaAndDepth(EmdArea emdArea, int depth);

    @Query(value = """
SELECT ST_AsGeoJSON(geom) AS geomGeoJson
FROM neighbor_area
WHERE emd_cd = :emdCd
  AND depth = :depth
LIMIT 1
""", nativeQuery = true)
    Object findNeighborAreaWithGeoJsonByEmdCdAndDepth(@Param("emdCd") Long emdCd, @Param("depth") int depth);

}

package com.team01.billage.map.repository;

import com.team01.billage.map.domain.NeighborArea;
import com.team01.billage.map.domain.EmdArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeighborAreaRepository extends JpaRepository<NeighborArea, Long> {
    // 특정 행정동 코드로 주변 지역 검색
    List<NeighborArea> findByEmdArea(EmdArea emdArea);

}

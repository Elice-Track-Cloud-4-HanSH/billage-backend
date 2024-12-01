package com.team01.billage.map.repository;

import com.team01.billage.map.domain.EmdArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmdAreaRepository extends JpaRepository<EmdArea, Long> {
    // 추가 쿼리 필요 시 선언
}

package com.team01.billage.map.repository;

import com.team01.billage.map.domain.ActivityArea;
import com.team01.billage.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityAreaRepository extends JpaRepository<ActivityArea, Long> {

    Optional<ActivityArea> findByUsers_Id(Long userId);  // 특정 사용자의 활동 지역 조회
}

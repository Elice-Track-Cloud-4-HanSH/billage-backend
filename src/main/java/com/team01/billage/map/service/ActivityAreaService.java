package com.team01.billage.map.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.map.domain.ActivityArea;
import com.team01.billage.map.domain.EmdArea;
import com.team01.billage.map.domain.NeighborArea;
import com.team01.billage.map.dto.ActivityAreaRequestDto;
import com.team01.billage.map.dto.ActivityAreaResponseDto;
import com.team01.billage.map.repository.ActivityAreaRepository;
import com.team01.billage.map.repository.EmdAreaRepository;
import com.team01.billage.map.repository.NeighborAreaRepository;
import com.team01.billage.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityAreaService {

    private final ActivityAreaRepository activityAreaRepository;
    private final EmdAreaRepository emdAreaRepository;
    private final NeighborAreaRepository neighborAreaRepository;

    // 활동 지역 설정
    public void setActivityArea(Long userId, ActivityAreaRequestDto requestDto) {
        EmdArea emdArea = emdAreaRepository.findById(requestDto.getEmdCd())
            .orElseThrow(() -> new CustomException(ErrorCode.EMD_AREA_NOT_FOUND));

        // NeighborArea 확인 (depth에 따른 처리)
        NeighborArea neighborArea = neighborAreaRepository.findByEmdAreaAndDepth(emdArea, requestDto.getDepth())
            .orElseThrow(() -> new CustomException(ErrorCode.NEIGHBOR_AREA_NOT_FOUND));

        // 기존 활동 지역 확인 및 업데이트
        ActivityArea activityArea = activityAreaRepository.findByUsers_Id(userId)
            .orElse(ActivityArea.builder()
                .users(Users.builder().id(userId).build())
                .emdArea(emdArea)
                .build());

        activityArea = ActivityArea.builder()
            .id(activityArea.getId()) // 기존 ID 유지
            .users(Users.builder().id(userId).build())
            .emdArea(neighborArea.getEmdArea()) // NeighborArea와 연관된 emdArea 사용
            .build();

        activityAreaRepository.save(activityArea);
    }

    // 활동 지역 조회
    public ActivityAreaResponseDto getActivityArea(Long userId) {
        ActivityArea activityArea = activityAreaRepository.findByUsers_Id(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.ACTIVITY_AREA_NOT_FOUND));

        EmdArea emdArea = activityArea.getEmdArea();

        return ActivityAreaResponseDto.builder()
            .emdCd(emdArea.getId())
            .emdNm(emdArea.getEmdNm())
            .sidoNm(emdArea.getSidoNm())
            .sggNm(emdArea.getSggNm())

            .build();
    }
}

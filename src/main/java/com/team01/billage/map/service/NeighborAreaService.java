package com.team01.billage.map.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.map.domain.EmdArea;
import com.team01.billage.map.domain.NeighborArea;
import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.repository.EmdAreaRepository;
import com.team01.billage.map.repository.NeighborAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NeighborAreaService {

    private final NeighborAreaRepository neighborAreaRepository;

    // emdCd와 depth를 받아서 NeighborArea를 조회하고 GeoJSON 데이터로 변환하여 반환

    @Transactional(readOnly = true)
    public EmdAreaGeoJsonResponseDto getNeighborAreaGeoJsonByEmdCdAndDepth(Long emdCd, int depth) {
        Object result = neighborAreaRepository.findNeighborAreaWithGeoJsonByEmdCdAndDepth(emdCd, depth);

        if (result == null) {
            throw new IllegalArgumentException("해당 조건에 맞는 NeighborArea가 없습니다.");
        }

        // Object를 배열로 변환하여 DTO 생성
        return new EmdAreaGeoJsonResponseDto(new Object[]{result});
    }
}

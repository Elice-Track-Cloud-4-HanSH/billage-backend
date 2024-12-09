package com.team01.billage.map.service;

import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.dto.EmdAreaGeoResponseDto;
import com.team01.billage.map.dto.EmdAreaResponseDto;
import com.team01.billage.map.repository.EmdAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmdAreaService {

    private final EmdAreaRepository emdAreaRepository;

    public Page<EmdAreaResponseDto> searchEmdAreas(String sggNm, String emdNm, Pageable pageable) {
        return emdAreaRepository.searchEmdAreas(sggNm, emdNm, pageable);
    }
    @Transactional(readOnly = true)
    public EmdAreaGeoJsonResponseDto getEmdAreaGeoJsonById(Long emdCd) {
        Object result = emdAreaRepository.findEmdAreaWithGeoJsonById(emdCd);

        if (result == null) {
            throw new IllegalArgumentException("존재하지 않는 emdCd입니다.");
        }

        // Object를 배열로 변환하여 DTO 생성
        return new EmdAreaGeoJsonResponseDto(new Object[]{result});
    }
}

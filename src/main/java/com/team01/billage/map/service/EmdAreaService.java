package com.team01.billage.map.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.EMD_AREA_NOT_FOUND);
        }

        // Object를 배열로 변환하여 DTO 생성
        return new EmdAreaGeoJsonResponseDto(new Object[]{result});
    }
}

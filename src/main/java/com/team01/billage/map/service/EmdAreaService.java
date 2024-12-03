package com.team01.billage.map.service;

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
}

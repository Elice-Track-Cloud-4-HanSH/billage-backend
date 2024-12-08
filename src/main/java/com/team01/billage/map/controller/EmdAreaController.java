package com.team01.billage.map.controller;

import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.dto.EmdAreaGeoResponseDto;
import com.team01.billage.map.dto.EmdAreaResponseDto;
import com.team01.billage.map.service.EmdAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emd-area")
@RequiredArgsConstructor
public class EmdAreaController {

    private final EmdAreaService emdAreaService;

    @GetMapping("/search")
    public ResponseEntity<Page<EmdAreaResponseDto>> searchEmdAreas(
        @RequestParam(required = false) String sggNm,
        @RequestParam(required = false) String emdNm,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmdAreaResponseDto> result = emdAreaService.searchEmdAreas(sggNm, emdNm, pageable);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{emdCd}")
    public ResponseEntity<EmdAreaGeoJsonResponseDto> getEmdArea(@PathVariable Long emdCd) {
        EmdAreaGeoJsonResponseDto result = emdAreaService.getEmdAreaGeoJsonById(emdCd);
        return ResponseEntity.ok(result);
    }
}

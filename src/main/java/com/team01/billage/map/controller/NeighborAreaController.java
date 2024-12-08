package com.team01.billage.map.controller;

import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.service.NeighborAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neighbor-area")
@RequiredArgsConstructor
public class NeighborAreaController {

    private final NeighborAreaService neighborAreaService;

    // 특정 emdCd와 depth로 NeighborArea 조회
    @GetMapping("/{emdCd}")
    public ResponseEntity<EmdAreaGeoJsonResponseDto> getNeighborArea(
        @PathVariable Long emdCd,
        @RequestParam int depth
    ) {
        EmdAreaGeoJsonResponseDto result = neighborAreaService.getNeighborAreaGeoJsonByEmdCdAndDepth(emdCd, depth);
        return ResponseEntity.ok(result);
    }
}

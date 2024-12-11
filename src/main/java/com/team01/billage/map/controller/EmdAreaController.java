package com.team01.billage.map.controller;

import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.dto.EmdAreaResponseDto;
import com.team01.billage.map.service.EmdAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "읍면동 검색", description = "특정 시군구(sggNm) 및 읍면동(emdNm)을 검색합니다.", tags = {"읍면동 관리"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content(mediaType = "application/json")),

    })
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
    @Operation(summary = "읍면동 GeoJSON 조회", description = "특정 읍면동의 GeoJSON 데이터를 조회합니다.", tags = {"읍면동 관리"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmdAreaGeoJsonResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "읍면동 데이터를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"읍면동 데이터를 찾을 수 없습니다.\",\"code\":\"EMD_AREA_NOT_FOUND\"}"))),
        
    })
    public ResponseEntity<EmdAreaGeoJsonResponseDto> getEmdArea(@PathVariable Long emdCd) {
        EmdAreaGeoJsonResponseDto result = emdAreaService.getEmdAreaGeoJsonById(emdCd);
        return ResponseEntity.ok(result);
    }
}

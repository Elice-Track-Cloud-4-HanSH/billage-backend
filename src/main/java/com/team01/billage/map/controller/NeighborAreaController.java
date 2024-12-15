package com.team01.billage.map.controller;

import com.team01.billage.map.dto.EmdAreaGeoJsonResponseDto;
import com.team01.billage.map.service.NeighborAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neighbor-area")
@RequiredArgsConstructor
public class NeighborAreaController {

    private final NeighborAreaService neighborAreaService;

    @GetMapping("/{emdCd}")
    @Operation(summary = "이웃 지역 조회", description = "특정 읍면동 코드(emdCd)와 깊이(depth)를 기반으로 이웃 지역의 GeoJSON 데이터를 조회합니다.", tags = {"이웃 지역 관리"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmdAreaGeoJsonResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "읍면동 데이터를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"읍면동 데이터를 찾을 수 없습니다.\",\"code\":\"EMD_AREA_NOT_FOUND\"}"))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<EmdAreaGeoJsonResponseDto> getNeighborArea(
        @PathVariable Long emdCd,
        @RequestParam int depth
    ) {
        EmdAreaGeoJsonResponseDto result = neighborAreaService.getNeighborAreaGeoJsonByEmdCdAndDepth(emdCd, depth);
        return ResponseEntity.ok(result);
    }
}
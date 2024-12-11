package com.team01.billage.map.controller;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.map.dto.ActivityAreaRequestDto;
import com.team01.billage.map.dto.ActivityAreaResponseDto;
import com.team01.billage.map.service.ActivityAreaService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.utils.DetermineUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-area")
@RequiredArgsConstructor
public class ActivityAreaController {

    private final ActivityAreaService activityAreaService;
    private final DetermineUser determineUser;

    @PostMapping
    @Operation(summary = "활동 지역 설정", description = "사용자의 활동 지역을 설정합니다.", tags = {"활동 지역"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "활동 지역 설정 성공", content = @Content()),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 행정구역 또는 이웃 지역", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"행정구역을 찾을 수 없습니다.\",\"code\":\"EMD_AREA_NOT_FOUND\"}"))),

    })
    public ResponseEntity<Void> setActivityArea(
        @RequestBody @Valid ActivityAreaRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Users user = determineUser.determineUser(userDetails);
        activityAreaService.setActivityArea(user.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @Operation(summary = "활동 지역 조회", description = "사용자의 활동 지역을 조회합니다.", tags = {"활동 지역"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "활동 지역 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActivityAreaResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))),

    })
    public ResponseEntity<ActivityAreaResponseDto> getActivityArea(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Users user = determineUser.determineUser(userDetails);
        ActivityAreaResponseDto responseDto = activityAreaService.getActivityArea(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping
    @Operation(summary = "활동 지역 삭제", description = "사용자의 활동 지역을 삭제합니다.", tags = {"활동 지역"})
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "활동 지역 삭제 성공", content = @Content()),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))),
        @ApiResponse(responseCode = "404", description = "활동 지역이 존재하지 않음", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"활동 지역을 찾을 수 없습니다.\",\"code\":\"ACTIVITY_AREA_NOT_FOUND\"}"))),

    })
    public ResponseEntity<Void> deleteActivityArea(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Users user = determineUser.determineUser(userDetails);
        activityAreaService.deleteActivityArea(user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

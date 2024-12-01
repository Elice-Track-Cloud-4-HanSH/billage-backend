package com.team01.billage.map.controller;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.map.dto.ActivityAreaRequestDto;
import com.team01.billage.map.dto.ActivityAreaResponseDto;
import com.team01.billage.map.service.ActivityAreaService;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-area")
@RequiredArgsConstructor
public class ActivityAreaController {

    private final ActivityAreaService activityAreaService;
    private final UserRepository userRepository;

    // 활동 지역 설정
    @PostMapping
    public ResponseEntity<Void> setActivityArea(
        @RequestBody ActivityAreaRequestDto requestDto,
        @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails에서 email을 가져오고, 이를 통해 userId 조회, 수정예정
        Users user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        activityAreaService.setActivityArea(user.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // 활동 지역 조회
    @GetMapping
    public ResponseEntity<ActivityAreaResponseDto> getActivityArea(
        @AuthenticationPrincipal UserDetails userDetails) {


        Users user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ActivityAreaResponseDto responseDto = activityAreaService.getActivityArea(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}

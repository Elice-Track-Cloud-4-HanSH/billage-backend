package com.team01.billage.map.controller;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.map.dto.ActivityAreaRequestDto;
import com.team01.billage.map.dto.ActivityAreaResponseDto;
import com.team01.billage.map.service.ActivityAreaService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.utils.DetermineUser;
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
    private final DetermineUser determineUser;

    // 활동 지역 설정
    @PostMapping
    public ResponseEntity<Void> setActivityArea(
        @RequestBody ActivityAreaRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Users user = determineUser.determineUser(userDetails);



        activityAreaService.setActivityArea(user.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // 활동 지역 조회
    @GetMapping
    public ResponseEntity<ActivityAreaResponseDto> getActivityArea(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }


        Users user = determineUser.determineUser(userDetails);


        ActivityAreaResponseDto responseDto = activityAreaService.getActivityArea(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    // 활동 지역 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteActivityArea(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 인증되지 않은 사용자 처리
        }

        Users user = determineUser.determineUser(userDetails);

        activityAreaService.deleteActivityArea(user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

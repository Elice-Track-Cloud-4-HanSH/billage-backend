package com.team01.billage.user_review.controller;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.product_review.dto.ReviewSubjectResponseDto;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user_review.service.UserReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-review")
public class UserReviewController {

    private final UserReviewService userReviewService;

    @Operation(
        summary = "사용자 후기 생성",
        description = "거래했던 상대방에 대한 후기를 생성합니다.",
        tags = {"사용자 후기"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "사용자 후기 생성 성공",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검사 실패",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "요청을 보낸 사용자가 대여기록의 구매자 또는 판매자 중 어느 하나와도 일치하지 않아 사용자 후기 작성 권한이 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 유저를 찾을 수 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 대여기록을 찾을 수 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "요청을 보낸 사용자가 해당 거래에 대해 작성한 사용자 후기가 이미 존재하는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        )
    })
    @PostMapping("/{rentalRecordId}")
    public ResponseEntity<Void> writeUserReview(
        @Parameter(description = "상품 후기 작성 요청 데이터", required = true)
        @Valid @RequestBody WriteReviewRequestDto writeReviewRequestDto,

        @Parameter(description = "대여 기록 ID", example = "1")
        @PathVariable("rentalRecordId") long rentalRecordId,

        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        userReviewService.createUserReview(writeReviewRequestDto, rentalRecordId,
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
        summary = "사용자 후기 조회",
        description = "요청을 보낸 사용자가 작성한 사용자 후기들을 조회합니다.",
        tags = {"사용자 후기"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용자 후기 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ShowReviewResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        )
    })
    @GetMapping
    public ResponseEntity<Slice<ShowReviewResponseDto>> showUserReview(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 유저 후기 ID입니다.", example = "123")
        @RequestParam(name = "lastStandard", required = false) Long lastStandard,

        @Parameter(description = "페이징 처리를 위한 Pageable 객체입니다.", example = "page=0&size=20&sort=createdAt,desc")
        Pageable pageable) {

        Slice<ShowReviewResponseDto> responseDtos = userReviewService.readUserReviews(
            userDetails.getId(), lastStandard, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @Operation(
        summary = "사용자 후기 대상 조회",
        description = "사용자 후기 작성 시 어떤 유저에 대한 후기인지 알기 위해 거래 상대 유저 정보를 조회합니다.",
        tags = {"사용자 후기"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "후기 대상 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReviewSubjectResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 대여기록을 찾을 수 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
    })
    @GetMapping("/{rentalRecordId}")
    public ResponseEntity<ReviewSubjectResponseDto> reviewSubject(
        @Parameter(description = "대여 기록 ID", example = "1")
        @PathVariable("rentalRecordId") long rentalRecordId,

        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        ReviewSubjectResponseDto responseDto = userReviewService.getReviewSubject(rentalRecordId,
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(
        summary = "사용자 후기 조회",
        description = "해당 유저에 대한 사용자 후기들을 조회합니다.",
        tags = {"사용자 후기"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용자 후기 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ShowReviewResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
    })
    @GetMapping("/target/{userId}")
    public ResponseEntity<Slice<ShowReviewResponseDto>> targetReview(
        @Parameter(description = "유저 ID", example = "1")
        @PathVariable("userId") long userId,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 유저 후기 ID입니다.", example = "123")
        @RequestParam(name = "lastStandard", required = false) Long lastStandard,

        @Parameter(description = "페이징 처리를 위한 Pageable 객체입니다.", example = "page=0&size=20&sort=createdAt,desc")
        Pageable pageable) {

        Slice<ShowReviewResponseDto> responseDtos = userReviewService.readTargetReviews(userId,
            lastStandard, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @Operation(
        summary = "사용자 후기 조회",
        description = "요청을 보낸 사용자에 대한 사용자 후기들을 조회합니다.",
        tags = {"사용자 후기"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "사용자 후기 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ShowReviewResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
    })
    @GetMapping("/target")
    public ResponseEntity<Slice<ShowReviewResponseDto>> targetReview(
        @Parameter(description = "유저 ID", example = "1")
        @AuthenticationPrincipal CustomUserDetails userDetails,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 유저 후기 ID입니다.", example = "123")
        @RequestParam(name = "lastStandard", required = false) Long lastStandard,

        @Parameter(description = "페이징 처리를 위한 Pageable 객체입니다.", example = "page=0&size=20&sort=createdAt,desc")
        Pageable pageable) {

        Slice<ShowReviewResponseDto> responseDtos = userReviewService.readTargetReviews(
            userDetails.getId(), lastStandard, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }
}

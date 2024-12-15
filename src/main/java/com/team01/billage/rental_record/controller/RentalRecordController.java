package com.team01.billage.rental_record.controller;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.product.service.ProductService;
import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import com.team01.billage.rental_record.dto.StartRentalRequestDto;
import com.team01.billage.rental_record.service.RentalRecordService;
import com.team01.billage.user.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rental-record")
public class RentalRecordController {

    private final RentalRecordService rentalRecordService;
    private final ProductService productService;

    @Operation(
        summary = "대여 기록 생성",
        description = "판매자가 해당 상품을 대여 중으로 변경하고 해당 상품에 대한 대여기록을 생성합니다.",
        tags = {"대여 기록"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "대여 기록 생성 성공",
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
            description = "요청을 보낸 사용자가 대여기록의 판매자와 일치하지 않아 대여 중으로 변경, 대여 기록 생성 권한이 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 채팅방을 찾을 수 없는 경우",
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
    @PostMapping
    public ResponseEntity<Void> startRental(
        @Parameter(description = "대여 기록 생성 요청 데이터", required = true)
        @Valid @RequestBody StartRentalRequestDto startRentalRequestDto,

        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        rentalRecordService.createRentalRecord(startRentalRequestDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
        summary = "대여 기록 조회",
        description = "요청을 보낸 사용자가 쿼리 파라미터(type)에 따라 빌려주는 중이거나 빌리는 중이거나 빌려줬었거나 빌렸었던 대여 기록들을 조회합니다.",
        tags = {"대여 기록"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "대여 기록 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ShowRecordResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "쿼리 파라미터가 유효하지 않은 유형인 경우",
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
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        )
    })
    @GetMapping
    public ResponseEntity<Slice<ShowRecordResponseDto>> showRentalRecord(
        @Parameter(description = "조회할 대여 기록의 유형을 지정합니다.", example = "대여중/판매")
        @RequestParam(name = "type") String type,

        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails,

        @Parameter(description = "페이징 처리를 위한 Pageable 객체입니다.", example = "page=0&size=20&sort=createdAt,desc")
        Pageable pageable,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 날짜입니다. null이면 첫 페이지로 간주됩니다.", example = "2024-12-06")
        @RequestParam(name = "lastStandard", required = false) LocalDate lastStandard,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 대여기록 ID입니다.", example = "123")
        @RequestParam(name = "lastId", required = false) Long lastId) {

        Slice<ShowRecordResponseDto> responseDtos = rentalRecordService.readRentalRecords(type,
            userDetails.getId(), pageable, lastStandard, lastId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }


    @Operation(
        summary = "거래 후보자 조회",
        description = "대여 기록 생성에서 거래 상대방을 선택하기 위해을 위해 해당 상품에 대해 채팅한 이력이 있는 거래 후보자들을 조회합니다.",
        tags = {"대여 기록"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "거래 후보자 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PurchasersResponseDto.class))
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
    @GetMapping("/set-to-rented/{productId}")
    public ResponseEntity<List<PurchasersResponseDto>> showPurchasers(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails,

        @Parameter(description = "상품 ID", example = "1")
        @PathVariable("productId") long productId) {

        List<PurchasersResponseDto> responseDtos = rentalRecordService.readPurchasers(
            userDetails.getId(), productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @Operation(
        summary = "대여 기록 수정",
        description = "대여 기록의 반납일을 null에서 현재 날짜로 업데이트합니다.",
        tags = {"대여 기록"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "반납 완료 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PurchasersResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "요청을 보낸 사용자가 해당 대여기록의 판매자가 아니어서 반납 완료를 할 권한이 없는 경우",
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
            description = "반납일이 null이 아니고 이미 반납일이 존재하는 경우",
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
    @PatchMapping("/{rentalRecordId}")
    public ResponseEntity<Void> returnCompleted(
        @Parameter(description = "대여 기록 ID", example = "1")
        @PathVariable("rentalRecordId") long rentalRecordId,

        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        rentalRecordService.updateRentalRecord(rentalRecordId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

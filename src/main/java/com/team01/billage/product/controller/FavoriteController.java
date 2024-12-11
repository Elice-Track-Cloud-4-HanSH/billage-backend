package com.team01.billage.product.controller;


import com.team01.billage.product.dto.*;
import com.team01.billage.product.service.FavoriteService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 해당 상품이 회원의 관심 상품인지 확인
    @GetMapping("/{productId}")
    @Operation(summary = "좋아요 확인", description = "특정 상품에 대한 사용자의 좋아요 여부를 확인합니다.", tags = {"관심 상품"})
    @ApiResponse(responseCode = "200", description = "좋아요 확인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckFavoriteResponseDto.class)))
    public ResponseEntity<CheckFavoriteResponseDto> checkFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.checkFavorite(userDetails, productId));
    }

    // 회원의 관심 상품 목록 조회
    @GetMapping
    @Operation(summary = "전체 관심 상품 조회", description = "회원의 전체 관심 상품을 조회합니다.", tags = {"관심 상품"})
    @ApiResponse(responseCode = "200", description = "전체 관심 상품 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "array", implementation = ProductResponseDto.class))))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    public ResponseEntity<List<ProductResponseDto>> findAllFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize){

        favoriteService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.findAllFavorite(userDetails.getId(), page, pageSize));
    }

    @PostMapping("/{productId}")
    @Operation(summary = "관심 상품 등록", description = "해당 상품을 관심 상품으로 등록합니다.", tags = {"관심 상품"})
    @ApiResponse(responseCode = "201", description = "관심 상품 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품을 찾을 수 없습니다.\",\"code\":\"PRODUCT_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "409", description = "이미 등록된 관심 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"이미 좋아요를 한 상품입니다.\",\"code\":\"LIKE_ALREADY_EXISTS\"}")))
    public ResponseEntity<FavoriteResponseDto> createFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        Users user = favoriteService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.createFavorite(user, productId));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "관심 상품 등록 해제", description = "관심 상품 등록을 해제합니다.", tags = {"관심 상품"})
    @ApiResponse(responseCode = "200", description = "관심 상품 삭제 성공", content = @Content())
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품을 찾을 수 없습니다.\",\"code\":\"PRODUCT_NOT_FOUND\"}")))
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        favoriteService.checkUser(userDetails.getId());

        favoriteService.deleteFavorite(userDetails.getId(), productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}

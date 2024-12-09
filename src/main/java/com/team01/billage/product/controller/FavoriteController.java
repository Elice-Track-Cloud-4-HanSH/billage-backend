package com.team01.billage.product.controller;


import com.team01.billage.product.dto.CheckFavoriteResponseDto;
import com.team01.billage.product.dto.FavoriteResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.service.FavoriteService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
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
    public ResponseEntity<CheckFavoriteResponseDto> checkFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        if(userDetails != null){
            System.out.println("회원: " + userDetails.getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.checkFavorite(userDetails, productId));
    }

    // 회원의 관심 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page){

        favoriteService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.findAllFavorite(userDetails.getId(), page));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteResponseDto> createFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        Users user = favoriteService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.createFavorite(user, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId){

        favoriteService.checkUser(userDetails.getId());

        favoriteService.deleteFavorite(userDetails.getId(), productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}

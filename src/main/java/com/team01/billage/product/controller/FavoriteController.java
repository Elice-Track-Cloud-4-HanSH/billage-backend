package com.team01.billage.product.controller;


import com.team01.billage.product.dto.CheckFavoriteResponseDto;
import com.team01.billage.product.dto.FavoriteResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.service.FavoriteService;
import com.team01.billage.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId){

        if(userDetails != null){
            System.out.println("회원 이메일: " + userDetails.getUsername());
        }
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.checkFavorite(userDetails, productId));
    }

    // 회원의 관심 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllFavorite(@AuthenticationPrincipal UserDetails userDetails){

        Users user = favoriteService.determineUser(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.findAllFavorite(user));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteResponseDto> createFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId){

        Users user = favoriteService.determineUser(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.createFavorite(user, productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId){

        Users user = favoriteService.determineUser(userDetails.getUsername());

        favoriteService.deleteFavorite(user, productId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}

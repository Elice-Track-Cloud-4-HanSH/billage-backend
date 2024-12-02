package com.team01.billage.product.controller;


import com.team01.billage.product.dto.CheckFavoriteResponseDto;
import com.team01.billage.product.dto.FavoriteResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 해당 상품이 회원의 관심 상품인지 확인
    @GetMapping("/{productId}")
    public ResponseEntity<CheckFavoriteResponseDto> checkFavorite(@PathVariable("productId") Long productId){
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.checkFavorite(productId));
    }

    // 회원의 관심 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllFavorite(){
        return ResponseEntity.status(HttpStatus.OK).body(favoriteService.findAllFavorite());
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteResponseDto> createFavorite(@PathVariable("productId") Long productId){
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.createFavorite(productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable("productId") Long productId){
        favoriteService.deleteFavorite(productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}

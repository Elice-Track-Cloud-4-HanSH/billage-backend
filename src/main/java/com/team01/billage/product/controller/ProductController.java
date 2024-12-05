package com.team01.billage.product.controller;

import com.team01.billage.product.dto.*;
import com.team01.billage.product.service.ProductImageService;
import com.team01.billage.product.service.ProductService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailWrapperResponseDto> findProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findProduct(userDetails, productId));
    }

    @GetMapping
    public ResponseEntity<ProductWrapperResponseDto> findAllProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "categoryId", required = false, defaultValue = "1") String categoryId,
            @RequestParam(value = "rentalStatus", required = false, defaultValue = "ALL") String rentalStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAllProducts(userDetails, categoryId, rentalStatus));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<Slice<OnSaleResponseDto>> findAllOnSale(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(value = "lastId", required = false) @DateTimeFormat(iso = ISO.DATE_TIME)
        LocalDateTime lastTime,
        Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getEmail(), lastTime, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute ProductRequestDto productRequestDto) {

        Users user = productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(user, productRequestDto));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId,
            @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto) {

        productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).
                body(productService.updateProduct(userDetails.getId(), productId, productUpdateRequestDto));
    }

    // 상품 이미지 삭제
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteProductImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("productId") String productId,
            @RequestBody List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos){

        productService.checkUser(userDetails.getId());

        productImageService.deleteProductImages(userDetails.getId(), productId, productImageDeleteRequestDtos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("productId") Long productId) {

        productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(userDetails.getId(), productId));
    }

}

package com.team01.billage.product.controller;

import com.team01.billage.product.dto.*;
import com.team01.billage.product.service.ProductImageService;
import com.team01.billage.product.service.ProductService;
import com.team01.billage.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailWrapperResponseDto> findProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findProduct(userDetails, productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "categoryId", required = false, defaultValue = "1") String categoryId,
            @RequestParam(value = "rentalStatus", required = false, defaultValue = "ALL") String rentalStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAllProducts(userDetails, categoryId, rentalStatus));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<List<OnSaleResponseDto>> findAllOnSale(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getUsername()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ProductRequestDto productRequestDto) {

        Users user = productService.determineUser(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(user, productRequestDto));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId,
            @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto) {

        Users user = productService.determineUser(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).
                body(productService.updateProduct(user, productId, productUpdateRequestDto));
    }

    // 상품 이미지 삭제
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteProductImages(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("productId") String productId,
            @RequestBody List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos){

        Users user = productService.determineUser(userDetails.getUsername());

        productImageService.deleteProductImages(user, productId, productImageDeleteRequestDtos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("productId") Long productId) {

        Users user = productService.determineUser(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(user, productId));
    }

}

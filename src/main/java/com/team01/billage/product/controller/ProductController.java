package com.team01.billage.product.controller;

import com.team01.billage.product.dto.*;
import com.team01.billage.product.service.ProductImageService;
import com.team01.billage.product.service.ProductService;
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
    public ResponseEntity<ProductDetailResponseDto> findProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllProducts(
            @RequestParam(value = "categoryId", required = false, defaultValue = "1") String categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAllProducts(categoryId));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<List<OnSaleResponseDto>> findAllOnSale(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getUsername()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> createProduct(@ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @PathVariable("productId") Long productId,
            @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).
                body(productService.updateProduct(productId, productUpdateRequestDto));
    }

    // 상품 이미지 삭제
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteProductImages(
            @RequestBody List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos){
        productImageService.deleteProductImages(productImageDeleteRequestDtos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productId));
    }

}

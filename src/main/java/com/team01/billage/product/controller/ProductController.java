package com.team01.billage.product.controller;

import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDeleteCheckDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductImageDeleteRequestDto;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.dto.ProductUpdateRequestDto;
import com.team01.billage.product.service.ProductImageService;
import com.team01.billage.product.service.ProductService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> findProduct(
        @PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllProducts(
        @RequestParam(required = false, value = "categoryId") String categoryId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllProducts(categoryId));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<Slice<OnSaleResponseDto>> findAllOnSale(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(value = "lastId", required = false) @DateTimeFormat(iso = ISO.DATE_TIME)
        LocalDateTime lastTime,
        Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getUsername(), lastTime, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> createProduct(
        @ModelAttribute ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(productRequestDto));
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
        @RequestBody List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos) {
        productImageService.deleteProductImages(productImageDeleteRequestDtos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(
        @PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productId));
    }

}

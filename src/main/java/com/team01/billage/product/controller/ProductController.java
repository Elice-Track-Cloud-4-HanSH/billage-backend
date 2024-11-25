package com.team01.billage.product.controller;

import com.team01.billage.product.dto.*;
import com.team01.billage.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> findProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductDetailResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @PathVariable("productId") Long productId, @RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productId, productRequestDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(@PathVariable("productId") Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productId));
    }

}

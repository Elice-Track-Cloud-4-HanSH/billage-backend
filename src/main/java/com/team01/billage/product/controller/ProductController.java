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
        ProductDetailResponseDto response = productService.findProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAllProducts() {
        List<ProductResponseDto> response = productService.findAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ProductDetailResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductDetailResponseDto response = productService.createProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
            @PathVariable("productId") Long productId, @RequestBody ProductRequestDto productRequestDto) {
        ProductDetailResponseDto response = productService.updateProduct(productId, productRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(@PathVariable("productId") Long productId) {
        ProductDeleteCheckDto productDeleteCheckDto = productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productDeleteCheckDto);
    }

}

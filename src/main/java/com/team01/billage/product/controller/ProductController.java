package com.team01.billage.product.controller;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDeleteCheckDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductDetailWrapperResponseDto;
import com.team01.billage.product.dto.ProductImageDeleteRequestDto;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.dto.ProductUpdateRequestDto;
import com.team01.billage.product.dto.ProductWrapperResponseDto;
import com.team01.billage.product.service.ProductImageService;
import com.team01.billage.product.service.ProductService;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        @RequestParam(value = "rentalStatus", required = false, defaultValue = "ALL") String rentalStatus,
        @RequestParam(value = "search", required = false, defaultValue = "ALL") String search,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllProducts(userDetails, categoryId, rentalStatus, search,
                page));
    }

    @Operation(
        summary = "상품 조회",
        description = "요청을 보낸 사용자가 판매 중인 상품들을 조회합니다.",
        tags = {"상품"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "상품 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = OnSaleResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        )
    })
    @GetMapping("/on-sale")
    public ResponseEntity<List<OnSaleResponseDto>> findAllOnSale(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getId()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> createProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @ModelAttribute ProductRequestDto productRequestDto) {

        Users user = productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(user, productRequestDto));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDetailResponseDto> updateProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("productId") Long productId,
        @Valid @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto) {

        productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).
            body(productService.updateProduct(userDetails.getId(), productId,
                productUpdateRequestDto));
    }

    // 상품 이미지 삭제
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteProductImages(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam("productId") String productId,
        @RequestBody List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos) {

        productService.checkUser(userDetails.getId());

        productImageService.deleteProductImages(userDetails.getId(), productId,
            productImageDeleteRequestDtos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("productId") Long productId) {

        productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.deleteProduct(userDetails.getId(), productId));
    }

}

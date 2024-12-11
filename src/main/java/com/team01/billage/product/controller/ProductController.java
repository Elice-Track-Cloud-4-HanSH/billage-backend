package com.team01.billage.product.controller;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDeleteCheckDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductDetailWrapperResponseDto;
import com.team01.billage.product.dto.ProductImageDeleteRequestDto;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.dto.ProductResponseDto;
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
    @Operation(summary = "상품 상세 조회", description = "특정 대여 상품을 상세 조회합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "200", description = "상품 상세 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailWrapperResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품을 찾을 수 없습니다.\",\"code\":\"PRODUCT_NOT_FOUND\"}")))
    public ResponseEntity<ProductDetailWrapperResponseDto> findProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("productId") Long productId) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findProduct(userDetails, productId));
    }

    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "전체 대여 상품을 조회합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "200", description = "전체 상품 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductWrapperResponseDto.class)))
    public ResponseEntity<ProductWrapperResponseDto> findAllProducts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(value = "categoryId", required = false, defaultValue = "1") String categoryId,
        @RequestParam(value = "rentalStatus", required = false, defaultValue = "ALL") String rentalStatus,
        @RequestParam(value = "search", required = false, defaultValue = "ALL") String search,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllProducts(userDetails, categoryId, rentalStatus, search,
                page, pageSize));
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
    public ResponseEntity<Slice<OnSaleResponseDto>> findAllOnSale(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails userDetails,

        @Parameter(description = "이전 요청에서 마지막으로 확인한 상품 수정 시간입니다.", example = "123")
        @RequestParam(name = "lastStandard", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime lastStandard,

        @Parameter(description = "페이징 처리를 위한 Pageable 객체입니다.", example = "page=0&size=20&sort=createdAt,desc")
        Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.findAllOnSale(userDetails.getId(), lastStandard, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "상품 등록", description = "대여 상품을 등록합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "201", description = "상품 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 카테고리를 찾을 수 없습니다.\",\"code\":\"CATEGORY_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"S3 업로드 중 오류가 발생했습니다.\",\"code\":\"PUT_OBJECT_EXCEPTION\"}")))
    public ResponseEntity<ProductDetailResponseDto> createProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @ModelAttribute ProductRequestDto productRequestDto) {

        Users user = productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(user, productRequestDto));
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "상품 수정", description = "대여 상품을 수정합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "200", description = "상품 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "대여 중인 상품은 수정 불가", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"현재 대여 중인 상품은 수정/삭제할 수 없습니다.\",\"code\":\"PRODUCT_MODIFICATION_NOT_ALLOWED\"}")))
    @ApiResponse(responseCode = "403", description = "권한이 없는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"접근 권한이 없습니다.\",\"code\":\"ACCESS_DENIED\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품을 찾을 수 없습니다.\",\"code\":\"PRODUCT_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 카테고리를 찾을 수 없습니다.\",\"code\":\"CATEGORY_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 기존 이미지", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품 이미지를 찾을 수 없습니다.\",\"code\":\"PRODUCT_IMAGE_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"S3 업로드 중 오류가 발생했습니다.\",\"code\":\"PUT_OBJECT_EXCEPTION\"}")))
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
    @Operation(summary = "상품 이미지 삭제", description = "대여 상품의 기존 이미지를 삭제합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "200", description = "상품 이미지 삭제 성공", content = @Content())
    @ApiResponse(responseCode = "403", description = "권한이 없는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"접근 권한이 없습니다.\",\"code\":\"ACCESS_DENIED\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 상품을 찾을 수 없습니다.\",\"code\":\"PRODUCT_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "500", description = "이미지 삭제 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"이미지 삭제 중 오류가 발생했습니다.\",\"code\":\"IO_EXCEPTION_ON_FILE_DELETE\"}")))
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
    @Operation(summary = "상품 삭제", description = "대여 상품을 삭제합니다.", tags = {"상품"})
    @ApiResponse(responseCode = "200", description = "상품 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDeleteCheckDto.class)))
    @ApiResponse(responseCode = "400", description = "대여 중인 상품은 삭제 불가", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"현재 대여 중인 상품은 수정/삭제할 수 없습니다.\",\"code\":\"PRODUCT_MODIFICATION_NOT_ALLOWED\"}")))
    @ApiResponse(responseCode = "403", description = "권한이 없는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"접근 권한이 없습니다.\",\"code\":\"ACCESS_DENIED\"}")))
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"해당 유저를 찾을 수 없습니다.\",\"code\":\"USER_NOT_FOUND\"}")))
    @ApiResponse(responseCode = "500", description = "이미지 삭제 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"이미지 삭제 중 오류가 발생했습니다.\",\"code\":\"IO_EXCEPTION_ON_FILE_DELETE\"}")))
    public ResponseEntity<ProductDeleteCheckDto> deleteProduct(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("productId") Long productId) {

        productService.checkUser(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK)
            .body(productService.deleteProduct(userDetails.getId(), productId));
    }

    @GetMapping("/neighbor-area")
    public ResponseEntity<List<ProductResponseDto>> findProductsInNeighborArea(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ProductResponseDto> products = productService.findProductsInNeighborArea(
            userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

}

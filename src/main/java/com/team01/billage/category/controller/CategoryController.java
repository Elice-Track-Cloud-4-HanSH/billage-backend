package com.team01.billage.category.controller;

import com.team01.billage.category.dto.CategoryResponseDto;
import com.team01.billage.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryResponseDto>> findCategories() {
        List<CategoryResponseDto> response = categoryService.findCategories();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

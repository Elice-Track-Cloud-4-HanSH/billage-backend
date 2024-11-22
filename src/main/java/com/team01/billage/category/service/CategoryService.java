package com.team01.billage.category.service;

import com.team01.billage.category.dto.CategoryResponseDto;
import com.team01.billage.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final String baseUrl = "https://localhost:8080";

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> findCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponseDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .imageUrl(baseUrl + category.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}

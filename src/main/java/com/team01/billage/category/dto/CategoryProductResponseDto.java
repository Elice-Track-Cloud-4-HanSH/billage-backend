package com.team01.billage.category.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProductResponseDto {

    private Long categoryId;
    private String categoryName;

}

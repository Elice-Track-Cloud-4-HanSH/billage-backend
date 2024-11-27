package com.team01.billage.product.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequestDto {

    private MultipartFile imageUrl;
    private String thumbnail; // Y/N

}

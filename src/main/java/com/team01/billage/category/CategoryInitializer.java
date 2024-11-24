package com.team01.billage.category;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Category> categories = Arrays.asList(
                new Category("전체", "/images/category/all.png"),
                new Category("가구/인테리어", "/images/category/furniture.png"),
                new Category("가전", "/images/category/home_appliance.png"),
                new Category("디지털 기기", "/images/category/digital.png"),
                new Category("스포츠/레저", "/images/category/leisure.png"),
                new Category("생활/주방", "/images/category/kitchen.png"),
                new Category("잡화", "/images/category/bag.png"),
                new Category("장비/공구", "/images/category/tool.png"),
                new Category("악기", "/images/category/piano.png"),
                new Category("도서", "/images/category/book.png"),
                new Category("취미/게임", "/images/category/game.png"),
                new Category("기타", "/images/category/etc.png")
        );
        categoryRepository.saveAll(categories);
    }
}

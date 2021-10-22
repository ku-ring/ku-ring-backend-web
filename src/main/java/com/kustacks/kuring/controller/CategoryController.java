package com.kustacks.kuring.controller;

import com.kustacks.kuring.controller.dto.CategoryListResponseDTO;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.service.CategoryService;
import com.kustacks.kuring.service.CategoryServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/notice/categories")
    public CategoryListResponseDTO getSupportedCategories() {

        List<Category> categories = categoryService.getCategories();
        List<String> categoryNames = categoryService.getCategoryNamesFromCategories(categories);

        return CategoryListResponseDTO.builder()
                .categories(categoryNames)
                .build();
    }
}

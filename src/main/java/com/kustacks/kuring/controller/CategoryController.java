package com.kustacks.kuring.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.CategoryListResponseDTO;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.error.APIException;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.service.FirebaseService;
import com.kustacks.kuring.service.CategoryService;
import com.kustacks.kuring.service.CategoryServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class CategoryController {

    private final CategoryService categoryService;
    private final FirebaseService firebaseService;

    public CategoryController(CategoryServiceImpl categoryService, FirebaseService firebaseService) {
        this.categoryService = categoryService;
        this.firebaseService = firebaseService;
    }

    @GetMapping("/notice/categories")
    public CategoryListResponseDTO getSupportedCategories() {

        List<Category> categories = categoryService.getCategories();
        List<String> categoryNames = categoryService.getCategoryNamesFromCategories(categories);

        return CategoryListResponseDTO.builder()
                .categories(categoryNames)
                .build();
    }

    @GetMapping("/notice/subscribe")
    public CategoryListResponseDTO getUserCategories(@RequestParam("id") String token) {
        // FCM에 이 토큰이 유효한지 확인
        // 유효하면? user-category 테이블 조회해서 카테고리 목록 생성
        // 유효하지 않으면? 404에러 리턴
        try {
            firebaseService.verifyToken(token);
        } catch (FirebaseMessagingException e) {
            throw new APIException(ErrorCode.API_FB_INVALID_TOKEN);
        }

        List<Category> categories = categoryService.getUserCategories(token);
        List<String> categoryNames = categoryService.getCategoryNamesFromCategories(categories);

        return CategoryListResponseDTO.builder()
                .categories(categoryNames)
                .build();
    }
}

package com.kustacks.kuring.category.presentation;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.category.common.dto.request.SubscribeCategoriesRequest;
import com.kustacks.kuring.category.common.dto.response.CategoryListResponse;
import com.kustacks.kuring.category.common.dto.response.SubscribeCategoriesResponse;
import com.kustacks.kuring.common.firebase.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService categoryService;
    private final FirebaseService firebaseService;

    @GetMapping("/notice/categories")
    public CategoryListResponse getSupportedCategories() {
        List<String> categoryNames = categoryService.lookUpSupportedCategories();
        return new CategoryListResponse(categoryNames);
    }

    @GetMapping("/notice/subscribe")
    public CategoryListResponse getUserCategories(@RequestParam("id") @NotBlank String token) {
        firebaseService.validationToken(token);
        List<String> categoryNames = categoryService.lookUpUserCategories(token);
        return new CategoryListResponse(categoryNames);
    }

    @PostMapping(value = "/notice/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubscribeCategoriesResponse editUserSubscribeCategories(@Valid @RequestBody SubscribeCategoriesRequest request) {
        firebaseService.validationToken(request.getId());
        categoryService.editSubscribeCategoryList(request.getId(), request.getCategories());
        return new SubscribeCategoriesResponse();
    }
}

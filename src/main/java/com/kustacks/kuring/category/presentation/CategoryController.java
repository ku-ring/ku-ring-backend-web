package com.kustacks.kuring.category.presentation;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.dto.CategoryListResponseDTO;
import com.kustacks.kuring.common.dto.SubscribeCategoriesRequestDTO;
import com.kustacks.kuring.common.dto.SubscribeCategoriesResponseDTO;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserCategory;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final FirebaseService firebaseService;

    public CategoryController(
            CategoryService categoryService,
            FirebaseService firebaseService,
            UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
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
        // 유효하지 않으면? 401에러 리턴

        if(token == null || token.equals("")) {
            throw new APIException(ErrorCode.API_INVALID_PARAM);
        }

        try {
            firebaseService.verifyToken(token);
        } catch (FirebaseMessagingException e) {
            throw new APIException(ErrorCode.API_FB_INVALID_TOKEN, e);
        }

        List<Category> categories = categoryService.getUserCategories(token);
        List<String> categoryNames = categoryService.getCategoryNamesFromCategories(categories);

        return CategoryListResponseDTO.builder()
                .categories(categoryNames)
                .build();
    }

    @PostMapping(value = "/notice/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SubscribeCategoriesResponseDTO subscribeCategories(@RequestBody SubscribeCategoriesRequestDTO requestDTO) {

        List<String> categories = requestDTO.getCategories();
        String token = requestDTO.getToken();

        if(categories == null || token == null || token.equals("")) {
            throw new APIException(ErrorCode.API_MISSING_PARAM);
        }

        // categories에 중복된 카테고리 검사 & 지원하지 않는 카테고리 검사
        try {
            categories = categoryService.verifyCategories(categories);
        } catch(InternalLogicException e) {
            throw new APIException(ErrorCode.API_INVALID_PARAM, e);
        }

        // FCM 토큰이 서버에 등록된 토큰인지 확인
        // 등록안되어 있다면 firebase에 유효한 토큰인지 확인 후 유효하다면 DB에 등록
        User user = userService.getUserByToken(token);
        if(user == null) {
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException | InternalLogicException e) {
                throw new APIException(ErrorCode.API_FB_INVALID_TOKEN, e);
            }

            user = userService.insertUserToken(token);
        }
        
        // 클라이언트가 등록 희망한 카테고리 목록과 DB에 등록되어있는 카테고리 목록을 비교
        // categories에는 있고 dbUserCategories에는 없는 건 새로 구독해야할 카테고리
        // dbUserCategories에는 있고 categories에는 없는 건 구독을 취소해야할 카테고리
        // 두 리스트 모두에 있는 카테고리는 무시. 아무 작업도 안해도 됨
        List<UserCategory> dbUserCategories = user.getUserCategories();
        Map<String, List<UserCategory>> resultMap = categoryService.compareCategories(categories, dbUserCategories, user);

        // 새로운 카테고리 구독 및 그렇지 않은 카테고리 구독 취소 작업 (DB, Firebase api 작업)
        // Transactional하게 동작하게 하기 위해 service layer에서 작업
        try {
            categoryService.updateUserCategory(token, resultMap);
        } catch (Exception e) {
            if(e instanceof FirebaseMessagingException) {
                throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
            } else {
                throw new APIException(ErrorCode.API_FB_SERVER_ERROR, e);
            }
        }

        return SubscribeCategoriesResponseDTO.builder().build();
    }
}

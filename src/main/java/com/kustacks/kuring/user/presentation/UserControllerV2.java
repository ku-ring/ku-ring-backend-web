package com.kustacks.kuring.user.presentation;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.feedback.business.FeedbackService;
import com.kustacks.kuring.notice.common.dto.CategoryNameDto;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.common.SubscribeCategoriesRequest;
import com.kustacks.kuring.user.common.SubscribeDepartmentsRequest;
import com.kustacks.kuring.user.common.dto.SaveFeedbackRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_SUBSCRIBE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SUBSCRIBE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.FEEDBACK_SAVE_SUCCESS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserControllerV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserService userService;
    private final CategoryService categoryService;
    private final FirebaseService firebaseService;
    private final FeedbackService feedbackService;


    @GetMapping("/subscriptions/categories")
    public ResponseEntity<BaseResponse<List<CategoryNameDto>>> lookupUserSubscribeCategories(@RequestHeader(USER_TOKEN_HEADER_KEY) String id) {
        firebaseService.validationToken(id);
        List<CategoryNameDto> categoryNamesDtos = convertCategoryNameDtoList(categoryService.lookUpUserCategories(id));

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS, categoryNamesDtos));
    }

    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<DepartmentNameDto>>> lookupUserSubscribeDepartments(@RequestHeader(USER_TOKEN_HEADER_KEY) String id) {
        firebaseService.validationToken(id);
        List<DepartmentNameDto> departmentNameDtos = convertDepartmentDtoList(userService.lookupSubscribeDepartmentList(id));

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS, departmentNameDtos));
    }

    @PostMapping(value = "/subscriptions/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeCategories(
            @Valid @RequestBody SubscribeCategoriesRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        firebaseService.validationToken(id);
        categoryService.editSubscribeCategoryList(id, request.getCategories());
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody SubscribeDepartmentsRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        firebaseService.validationToken(id);
        userService.editSubscribeDepartmentList(id, request.getDepartments());
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<BaseResponse<Void>> saveFeedback(
            @Valid @RequestBody SaveFeedbackRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) throws APIException {
        firebaseService.validationToken(id);
        feedbackService.saveFeedback(id, request.getContent());

        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SAVE_SUCCESS, null));
    }

    private List<CategoryNameDto> convertCategoryNameDtoList(List<CategoryName> categoryNamesList) {
        return categoryNamesList.stream()
                .map(CategoryNameDto::from)
                .collect(Collectors.toList());
    }

    private List<DepartmentNameDto> convertDepartmentDtoList(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .map(DepartmentNameDto::from)
                .collect(Collectors.toList());
    }
}

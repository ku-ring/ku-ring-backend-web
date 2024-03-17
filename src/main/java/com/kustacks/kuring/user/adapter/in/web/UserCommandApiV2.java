package com.kustacks.kuring.user.adapter.in.web;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.BOOKMAKR_SAVE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_SUBSCRIBE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_SUBSCRIBE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.FEEDBACK_SAVE_SUCCESS;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentsSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserFeedbackRequest;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User-Command", description = "사용자가 주체가 되는 정보 수정")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users")
class UserCommandApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserCommandUseCase userCommandUseCase;

    @Operation(summary = "사용자 카테고리 수정", description = "사용자가 구독한 카테고리 목록을 추가, 삭제 합니다")
    @SecurityRequirement(name = "User-Token")
    @PostMapping(value = "/subscriptions/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeCategories(
            @Valid @RequestBody UserCategoriesSubscribeRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.editSubscribeCategories(new UserCategoriesSubscribeCommand(id, request.categories()));
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SUBSCRIBE_SUCCESS, null));
    }

    @Operation(summary = "사용자 학과 수정", description = "사용자가 구독한 학과 목록을 추가, 삭제 합니다")
    @SecurityRequirement(name = "User-Token")
    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody UserDepartmentsSubscribeRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.editSubscribeDepartments(new UserDepartmentsSubscribeCommand(id, request.departments()));
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }

    @Operation(summary = "사용자 피드백 작성", description = "사용자가 피드백을 작성하여 저장합니다")
    @SecurityRequirement(name = "User-Token")
    @PostMapping("/feedbacks")
    public ResponseEntity<BaseResponse<Void>> saveFeedback(
            @Valid @RequestBody UserFeedbackRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.saveFeedback(new UserFeedbackCommand(id, request.content()));
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SAVE_SUCCESS, null));
    }

    @Operation(summary = "사용자 북마크 작성", description = "사용자가 원하는 공지를 북마크 하여 저장합니다")
    @SecurityRequirement(name = "User-Token")
    @PostMapping("/bookmarks")
    public ResponseEntity<BaseResponse<Void>> saveBookmark(
            @Valid @RequestBody UserBookmarkRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.saveBookmark(new UserBookmarkCommand(id, request.articleId()));
        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMAKR_SAVE_SUCCESS, null));
    }
}

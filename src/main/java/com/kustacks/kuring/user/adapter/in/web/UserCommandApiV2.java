package com.kustacks.kuring.user.adapter.in.web;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users")
class UserCommandApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserCommandUseCase userCommandService;

    @PostMapping(value = "/subscriptions/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeCategories(
            @Valid @RequestBody UserCategoriesSubscribeRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandService.editSubscribeCategories(new UserCategoriesSubscribeCommand(id, request.categories()));
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody UserDepartmentsSubscribeRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandService.editSubscribeDepartments(new UserDepartmentsSubscribeCommand(id, request.departments()));
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<BaseResponse<Void>> saveFeedback(
            @Valid @RequestBody UserFeedbackRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandService.saveFeedback(new UserFeedbackCommand(id, request.content()));
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SAVE_SUCCESS, null));
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<BaseResponse<Void>> saveBookmark(
            @Valid @RequestBody UserBookmarkRequest request,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        userCommandService.saveBookmark(new UserBookmarkCommand(id, request.articleId()));
        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMAKR_SAVE_SUCCESS, null));
    }
}

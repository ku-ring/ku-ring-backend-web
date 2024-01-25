package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoryNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentNameResponse;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/users", produces = MediaType.APPLICATION_JSON_VALUE)
class UserQueryApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserQueryUseCase userQueryService;

    @GetMapping("/subscriptions/categories")
    public ResponseEntity<BaseResponse<List<UserCategoryNameResponse>>> lookupUserSubscribeCategories(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserCategoryNameResponse> responses = userQueryService.lookupSubscribeCategories(userToken)
                .stream()
                .map(UserCategoryNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<UserDepartmentNameResponse>>> lookupUserSubscribeDepartments(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserDepartmentNameResponse> responses = userQueryService.lookupSubscribeDepartments(userToken)
                .stream()
                .map(UserDepartmentNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<BaseResponse<List<UserBookmarkResponse>>> lookupUserBookmarks(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserBookmarkResponse> responses = userQueryService.lookupUserBookmarkedNotices(userToken)
                .stream()
                .map(UserBookmarkResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMARK_LOOKUP_SUCCESS, responses));
    }
}

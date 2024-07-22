package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoryNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentNameResponse;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Tag(name = "User-Query", description = "사용자가 주체가 되는 정보 조회")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users")
class UserQueryApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final UserQueryUseCase userQueryUseCase;

    @Operation(summary = "사용자 카테고리 조회", description = "사용자가 구독한 카테고리 목록을 조회합니다")
    @SecurityRequirement(name = USER_TOKEN_HEADER_KEY)
    @GetMapping("/subscriptions/categories")
    public ResponseEntity<BaseResponse<List<UserCategoryNameResponse>>> lookupUserSubscribeCategories(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserCategoryNameResponse> responses = userQueryUseCase.lookupSubscribeCategories(userToken)
                .stream()
                .map(UserCategoryNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @Operation(summary = "사용자 학과 조회", description = "사용자가 구독한 학과의 목록을 조회합니다")
    @SecurityRequirement(name = USER_TOKEN_HEADER_KEY)
    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<UserDepartmentNameResponse>>> lookupUserSubscribeDepartments(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserDepartmentNameResponse> responses = userQueryUseCase.lookupSubscribeDepartments(userToken)
                .stream()
                .map(UserDepartmentNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @Operation(summary = "사용자 북마크 조회", description = "사용자가 북마크한 공지의 목록을 조회합니다")
    @SecurityRequirement(name = USER_TOKEN_HEADER_KEY)
    @GetMapping("/bookmarks")
    public ResponseEntity<BaseResponse<List<UserBookmarkResponse>>> lookupUserBookmarks(
            @RequestHeader(USER_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserBookmarkResponse> responses = userQueryUseCase.lookupUserBookmarkedNotices(userToken)
                .stream()
                .map(UserBookmarkResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMARK_LOOKUP_SUCCESS, responses));
    }
}

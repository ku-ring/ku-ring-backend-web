package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.adapter.in.web.dto.UserAIAskCountResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoryNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserInfoResponse;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserAIAskCountResult;
import com.kustacks.kuring.user.application.port.in.dto.UserInfoResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ASK_COUNT_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.BOOKMARK_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.USER_INFO_LOOKUP_SUCCESS;

@Tag(name = "User-Query", description = "사용자가 주체가 되는 정보 조회")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users")
class UserQueryApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";
    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final UserQueryUseCase userQueryUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "사용자 카테고리 조회", description = "사용자가 구독한 카테고리 목록을 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/subscriptions/categories")
    public ResponseEntity<BaseResponse<List<UserCategoryNameResponse>>> lookupUserSubscribeCategories(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserCategoryNameResponse> responses = userQueryUseCase.lookupSubscribeCategories(userToken)
                .stream()
                .map(UserCategoryNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @Operation(summary = "사용자 학과 조회", description = "사용자가 구독한 학과의 목록을 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<UserDepartmentNameResponse>>> lookupUserSubscribeDepartments(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserDepartmentNameResponse> responses = userQueryUseCase.lookupSubscribeDepartments(userToken)
                .stream()
                .map(UserDepartmentNameResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS, responses));
    }

    @Operation(summary = "사용자 북마크 조회", description = "사용자가 북마크한 공지의 목록을 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/bookmarks")
    public ResponseEntity<BaseResponse<List<UserBookmarkResponse>>> lookupUserBookmarks(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        List<UserBookmarkResponse> responses = userQueryUseCase.lookupUserBookmarkedNotices(userToken)
                .stream()
                .map(UserBookmarkResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMARK_LOOKUP_SUCCESS, responses));
    }

    @Operation(summary = "사용자 질문 가능횟수 조회", description = "사용자의 남은 질문횟수와 가능한 질문 횟수를 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/ask-counts")
    public ResponseEntity<BaseResponse<UserAIAskCountResponse>> lookupUserAIAskCount(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        UserAIAskCountResult result = userQueryUseCase.lookupUserAIAskCount(userToken);
        UserAIAskCountResponse response = UserAIAskCountResponse.from(result);
        return ResponseEntity.ok().body(new BaseResponse<>(ASK_COUNT_LOOKUP_SUCCESS, response));
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping("/user-me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> lookupUserInfo(
                        @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String jwtToken = extract(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        UserInfoResult result = userQueryUseCase.lookupUserInfo(email);
        UserInfoResponse response = UserInfoResponse.from(result);
        return ResponseEntity.ok().body(new BaseResponse<>(USER_INFO_LOOKUP_SUCCESS, response));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }

    private String extract(String value, AuthorizationType type) {
        String typeToLowerCase = type.toLowerCase();
        int typeLength = typeToLowerCase.length();

        if ((value.toLowerCase().startsWith(typeToLowerCase))) {
            String authHeaderValue = value.substring(typeLength).trim();
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }

        return Strings.EMPTY;
    }
}

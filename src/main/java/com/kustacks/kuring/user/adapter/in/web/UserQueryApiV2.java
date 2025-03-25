package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.dto.ResponseCodeAndMessages;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.adapter.in.web.dto.UserAIAskCountResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoryNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentNameResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserInfoResponse;
import com.kustacks.kuring.user.application.port.in.UserQueryUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserAIAskCountResult;
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
import java.util.function.Function;
import java.util.function.Supplier;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;
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
        return lookupAndConvertResponse(
                () -> userQueryUseCase.lookupSubscribeCategories(userToken),
                result -> result.stream()
                        .map(UserCategoryNameResponse::from)
                        .toList(),
                CATEGORY_USER_SUBSCRIBES_LOOKUP_SUCCESS);
    }

    @Operation(summary = "사용자 학과 조회", description = "사용자가 구독한 학과의 목록을 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/subscriptions/departments")
    public ResponseEntity<BaseResponse<List<UserDepartmentNameResponse>>> lookupUserSubscribeDepartments(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        return lookupAndConvertResponse(
                () -> userQueryUseCase.lookupSubscribeDepartments(userToken),
                result -> result.stream()
                        .map(UserDepartmentNameResponse::from)
                        .toList(),
                DEPARTMENTS_USER_SUBSCRIBES_LOOKUP_SUCCESS
        );
    }

    @Operation(summary = "사용자 북마크 조회", description = "사용자가 북마크한 공지의 목록을 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @GetMapping("/bookmarks")
    public ResponseEntity<BaseResponse<List<UserBookmarkResponse>>> lookupUserBookmarks(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken
    ) {
        return lookupAndConvertResponse(
                () -> userQueryUseCase.lookupUserBookmarkedNotices(userToken),
                result -> result.stream()
                        .map(UserBookmarkResponse::from)
                        .toList(),
                BOOKMARK_LOOKUP_SUCCESS
        );
    }

    @Operation(summary = "사용자 질문 가능횟수 조회", description = "사용자의 남은 질문횟수와 가능한 질문 횟수를 조회합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping("/ask-counts")
    public ResponseEntity<BaseResponse<UserAIAskCountResponse>> lookupUserAIAskCount(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken,
            @RequestHeader(value = AuthorizationExtractor.AUTHORIZATION, required = false) String bearerToken
    ) {
        Supplier<UserAIAskCountResult> lookUpQuery = null;

        if (bearerToken == null) {
            lookUpQuery = () -> userQueryUseCase.lookupUserAIAskCountWithFcmToken(userToken);
        } else {
            String accessToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
            String email = validateJwtAndGetEmail(accessToken);

            lookUpQuery = () -> userQueryUseCase.lookupUserAIAskCountWithEmail(email);
        }

        return lookupAndConvertResponse(
                lookUpQuery,
                UserAIAskCountResponse::from,
                ASK_COUNT_LOOKUP_SUCCESS
        );
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping("/user-me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> lookupUserInfo(
                        @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String jwtToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        return lookupAndConvertResponse(
                () -> userQueryUseCase.lookupUserInfo(email),
                UserInfoResponse::from,
                USER_INFO_LOOKUP_SUCCESS
        );
    }

    private <T, R> ResponseEntity<BaseResponse<R>> lookupAndConvertResponse(
            Supplier<T> lookupQuery,
            Function<T, R> responseConverter,
            ResponseCodeAndMessages responseCodeAndMessages
    ) {
        T result = lookupQuery.get();
        R response = responseConverter.apply(result);
        return ResponseEntity.ok().body(new BaseResponse<>(responseCodeAndMessages, response));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }
}

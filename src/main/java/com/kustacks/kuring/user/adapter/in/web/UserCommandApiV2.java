package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.adapter.in.web.dto.UserBookmarkRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserCategoriesSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserDepartmentsSubscribeRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserFeedbackRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserLoginRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserLoginResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserPasswordModifyRequest;
import com.kustacks.kuring.user.adapter.in.web.dto.UserSignupRequest;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginResult;
import com.kustacks.kuring.user.application.port.in.dto.UserLogoutCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserPasswordModifyCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSignupCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

@Tag(name = "User-Command", description = "사용자가 주체가 되는 정보 수정")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users")
class UserCommandApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";
    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final UserCommandUseCase userCommandUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "사용자 카테고리 수정", description = "사용자가 구독한 카테고리 목록을 추가, 삭제 합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping(value = "/subscriptions/categories", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeCategories(
            @Valid @RequestBody UserCategoriesSubscribeRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.editSubscribeCategories(new UserCategoriesSubscribeCommand(id, request.categories()));
        return ResponseEntity.ok().body(new BaseResponse<>(CATEGORY_SUBSCRIBE_SUCCESS, null));
    }

    @Operation(summary = "사용자 학과 수정", description = "사용자가 구독한 학과 목록을 추가, 삭제 합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping(value = "/subscriptions/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> editUserSubscribeDepartments(
            @Valid @RequestBody UserDepartmentsSubscribeRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.editSubscribeDepartments(new UserDepartmentsSubscribeCommand(id, request.departments()));
        return ResponseEntity.ok().body(new BaseResponse<>(DEPARTMENTS_SUBSCRIBE_SUCCESS, null));
    }

    @Operation(summary = "사용자 피드백 작성", description = "사용자가 피드백을 작성하여 저장합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping("/feedbacks")
    public ResponseEntity<BaseResponse<Void>> saveFeedback(
            @Valid @RequestBody UserFeedbackRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.saveFeedback(new UserFeedbackCommand(id, request.content()));
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SAVE_SUCCESS, null));
    }

    @Operation(summary = "사용자 북마크 작성", description = "사용자가 원하는 공지를 북마크 하여 저장합니다")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping("/bookmarks")
    public ResponseEntity<BaseResponse<Void>> saveBookmark(
            @Valid @RequestBody UserBookmarkRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.saveBookmark(new UserBookmarkCommand(id, request.articleId()));
        return ResponseEntity.ok().body(new BaseResponse<>(BOOKMAKR_SAVE_SUCCESS, null));
    }

    @Operation(summary = "사용자 회원가입", description = "사용자가 회원가입합니다.")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<Void>> signup(
            @RequestBody UserSignupRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        userCommandUseCase.signupUser(new UserSignupCommand(request.email(), request.password()));
        return ResponseEntity.ok().body(new BaseResponse<>(USER_SIGNUP, null));
    }

    //TODO : 로그인 로그아웃 시 Filter에서 진행하도록 변경필요
    @Operation(summary = "사용자 로그인", description = "사용자가 로그인합니다.")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse<UserLoginResponse>> login(
            @RequestBody UserLoginRequest request,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id
    ) {
        UserLoginResult result = userCommandUseCase.login(new UserLoginCommand(id, request.email(), request.password()));
        UserLoginResponse response = UserLoginResponse.from(result);
        return ResponseEntity.ok().body(new BaseResponse<>(USER_LOGIN, response));
    }

    @Operation(summary = "사용자 로그아웃", description = "사용자가 로그아웃합니다.")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PostMapping(value = "/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id,
            @RequestHeader (AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String jwtToken = extract(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        userCommandUseCase.logout(new UserLogoutCommand(id, email));
        return ResponseEntity.ok().body(new BaseResponse<>(USER_LOGOUT, null));
    }

    @Operation(summary = "사용자 비밀번호 변경", description = "사용자가 비밀번호 변경합니다.")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PatchMapping(value = "/password")
    public ResponseEntity<BaseResponse<Void>> modifyPassword(
                @RequestBody UserPasswordModifyRequest request,
                @RequestHeader (value = AuthorizationExtractor.AUTHORIZATION, required = false) String bearerToken
    ) {
        if (bearerToken == null) {
            userCommandUseCase.changePassword(new UserPasswordModifyCommand(request.email(), request.password()));
        }else{
            String jwtToken = extract(bearerToken, AuthorizationType.BEARER);
            String email = validateJwtAndGetEmail(jwtToken);
            userCommandUseCase.changePassword(new UserPasswordModifyCommand(email, request.password()));
        }

        return ResponseEntity.ok().body(new BaseResponse<>(USER_PASSWORD_MODIFY, null));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }

    private static String extract(String value, AuthorizationType type) {
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

package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;

import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubListResponse;
import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.ClubSubscriptionUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;
import com.kustacks.kuring.club.application.port.in.dto.SubscribedClubListCommand;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.adapter.in.web.dto.UserClubSubscriptionCountResponse;
import com.kustacks.kuring.user.adapter.in.web.dto.UserClubSubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_SUBSCRIPTION_ADD_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_SUBSCRIPTION_DELETE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_SUBSCRIPTION_LIST_SEARCH_SUCCESS;

@Tag(name = "User-Club-Subscription", description = "동아리 구독")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/users/subscriptions/clubs")
class UserClubSubscriptionApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";
    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final ClubSubscriptionUseCase clubSubscriptionUseCase;
    private final ClubQueryUseCase clubQueryUseCase;

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "사용자 동아리 구독 추가")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PostMapping
    public ResponseEntity<BaseResponse<UserClubSubscriptionCountResponse>> addSubscription(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken,
            @RequestHeader(AuthorizationExtractor.AUTHORIZATION) String bearerToken,
            @Valid @RequestBody UserClubSubscriptionRequest request
    ) {
        String email = validateJwtAndGetEmail(extractAuthorizationValue(bearerToken, AuthorizationType.BEARER));
        long subscriptionCount = clubSubscriptionUseCase.addSubscription(new ClubSubscriptionCommand(email, request.id()));

        return ResponseEntity.ok(new BaseResponse<>(CLUB_SUBSCRIPTION_ADD_SUCCESS, new UserClubSubscriptionCountResponse(subscriptionCount)));
    }

    @Operation(summary = "사용자 동아리 구독 제거")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @DeleteMapping("/{clubId}")
    public ResponseEntity<BaseResponse<UserClubSubscriptionCountResponse>> deleteSubscription(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken,
            @RequestHeader(AuthorizationExtractor.AUTHORIZATION) String bearerToken,
            @PathVariable Long clubId
    ) {
        String email = validateJwtAndGetEmail(extractAuthorizationValue(bearerToken, AuthorizationType.BEARER));
        long subscriptionCount = clubSubscriptionUseCase.removeSubscription(new ClubSubscriptionCommand(email, clubId));

        return ResponseEntity.ok(new BaseResponse<>(CLUB_SUBSCRIPTION_DELETE_SUCCESS, new UserClubSubscriptionCountResponse(subscriptionCount)));
    }

    @Operation(summary = "구독한 동아리 목록 조회")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping
    public ResponseEntity<BaseResponse<ClubListResponse>> getMySubscriptions(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken,
            @RequestHeader(AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String email = validateJwtAndGetEmail(extractAuthorizationValue(bearerToken, AuthorizationType.BEARER));

        SubscribedClubListCommand command = new SubscribedClubListCommand(email);

        ClubListResult result = clubQueryUseCase.getSubscribedClubs(command);

        ClubListResponse response = ClubListResponse.from(result);

        return ResponseEntity.ok(new BaseResponse<>(CLUB_SUBSCRIPTION_LIST_SEARCH_SUCCESS, response));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }
}

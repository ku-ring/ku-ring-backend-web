package com.kustacks.kuring.user.adapter.in.web;

import com.kustacks.kuring.auth.authentication.*;
import com.kustacks.kuring.auth.token.*;
import com.kustacks.kuring.club.adapter.in.web.dto.*;
import com.kustacks.kuring.club.application.port.in.*;
import com.kustacks.kuring.club.application.port.in.dto.*;
import com.kustacks.kuring.common.annotation.*;
import com.kustacks.kuring.common.dto.*;
import com.kustacks.kuring.common.exception.*;
import com.kustacks.kuring.common.exception.code.*;
import com.kustacks.kuring.user.adapter.in.web.dto.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.*;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

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

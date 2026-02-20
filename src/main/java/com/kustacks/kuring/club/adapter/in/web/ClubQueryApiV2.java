package com.kustacks.kuring.club.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubDetailResponse;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubDivisionListResponse;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubDivisionResponse;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubListResponse;
import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.data.Cursor;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_DETAIL_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_DIVISION_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_LIST_SEARCH_SUCCESS;

@Tag(name = "Club-Query", description = "동아리 정보 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/clubs")
public class ClubQueryApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";
    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final JwtTokenProvider jwtTokenProvider;
    private final ClubQueryUseCase clubQueryUseCase;

    @Operation(summary = "동아리 소속 목록 조회", description = "서버가 지원하는 동아리 소속 목록을 조회합니다")
    @GetMapping("/divisions")
    public ResponseEntity<BaseResponse<ClubDivisionListResponse>> getSupportedClubDivisions() {

        List<ClubDivisionResponse> divisions = clubQueryUseCase.getClubDivisions()
                .stream()
                .map(ClubDivisionResponse::from)
                .toList();

        ClubDivisionListResponse response = new ClubDivisionListResponse(divisions);

        return ResponseEntity.ok().body(new BaseResponse<>(CLUB_DIVISION_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "동아리 목록 조회", description = "필터 조건에 맞는 동아리 목록을 커서 페이징으로 조회합니다")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping
    public ResponseEntity<BaseResponse<ClubListResponse>> getClubs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(30) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestHeader(value = AuthorizationExtractor.AUTHORIZATION, required = false) String bearerToken
    ) {
        Long loginUserId = null;

        if (bearerToken != null) {
            String jwt = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);

            if (jwtTokenProvider.validateToken(jwt)) {
                loginUserId = Long.parseLong(jwtTokenProvider.getPrincipal(jwt));
            }
        }

        ClubListCommand command = new ClubListCommand(category, division, Cursor.from(cursor), size, sortBy);

        ClubListResult result = clubQueryUseCase.getClubs(command, loginUserId);

        ClubListResponse response = ClubListResponse.from(result);

        return ResponseEntity.ok().body(new BaseResponse<>(CLUB_LIST_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "동아리 상세 조회", description = "특정 동아리의 상세 정보를 조회합니다.")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ClubDetailResponse>> getClubDetail(
            @PathVariable Long id,
            @RequestHeader(value = FCM_TOKEN_HEADER_KEY, required = false) String userToken,
            @RequestHeader(value = AuthorizationExtractor.AUTHORIZATION, required = false) String bearerToken
    ) {
        Long loginUserId = null;

        if (bearerToken != null) {
            String jwt = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);

            if (!jwtTokenProvider.validateToken(jwt)) {
                throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
            }

            loginUserId = Long.parseLong(jwtTokenProvider.getPrincipal(jwt));
        }


        ClubDetailResult result = clubQueryUseCase.getClubDetail(id, userToken, loginUserId);

        ClubDetailResponse response = ClubDetailResponse.from(result);

        return ResponseEntity.ok().body(new BaseResponse<>(CLUB_DETAIL_SEARCH_SUCCESS, response));
    }
}
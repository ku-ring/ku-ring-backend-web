package com.kustacks.kuring.club.adapter.in.web;

import com.kustacks.kuring.club.adapter.in.web.dto.ClubDivisionListResponse;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubDivisionResponse;
import com.kustacks.kuring.club.adapter.in.web.dto.ClubListResponse;
import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.data.Cursor;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_DIVISION_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_LIST_SEARCH_SUCCESS;

@Tag(name = "Club-Query", description = "동아리 정보 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/clubs")
public class ClubQueryApiV2 {

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
    @GetMapping
    public ResponseEntity<BaseResponse<ClubListResponse>> getClubs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(30) int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        ClubListCommand command = new ClubListCommand(category, division, Cursor.from(cursor), size, sortBy);

        ClubListResult result = clubQueryUseCase.getClubs(command);

        ClubListResponse response = ClubListResponse.from(result);

        return ResponseEntity.ok().body(new BaseResponse<>(CLUB_LIST_SEARCH_SUCCESS, response));
    }


}
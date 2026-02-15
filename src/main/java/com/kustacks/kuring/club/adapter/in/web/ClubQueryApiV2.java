package com.kustacks.kuring.club.adapter.in.web;

import com.kustacks.kuring.club.adapter.in.web.dto.ClubDivisionResponse;
import com.kustacks.kuring.club.application.port.in.ClubQueryUseCase;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CLUB_DIVISION_SEARCH_SUCCESS;

@Tag(name = "Club Query", description = "동아리 정보 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/clubs")
public class ClubQueryApiV2 {

    private final ClubQueryUseCase clubQueryUseCase;

    @Operation(summary = "동아리 소속 목록 조회", description = "서버가 지원하는 동아리 소속 목록을 조회합니다")
    @GetMapping("/divisions")
    public ResponseEntity<BaseResponse<List<ClubDivisionResponse>>> getSupportedClubDivisions() {

        List<ClubDivisionResponse> divisions = clubQueryUseCase.getClubDivisions()
                .stream()
                .map(ClubDivisionResponse::from)
                .toList();

        return ResponseEntity.ok().body(new BaseResponse<>(CLUB_DIVISION_SEARCH_SUCCESS, divisions));
    }

}
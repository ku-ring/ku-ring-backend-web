package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.domain.ClubDivision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("서비스 : ClubQueryService")
class ClubQueryServiceTest {

    private final ClubQueryService clubQueryService = new ClubQueryService();

    @Test
    @DisplayName("동아리 소속 목록을 정상적으로 조회한다")
    void getClubDivisions_success() {
        // when
        List<ClubDivisionResult> results = clubQueryService.getClubDivisions();

        List<ClubDivisionResult> expected =
                Arrays.stream(ClubDivision.values())
                        .map(d -> new ClubDivisionResult(d.getName(), d.getKorName()))
                        .toList();

        // then
        assertThat(results)
                .containsExactlyInAnyOrderElementsOf(expected);
    }

}

package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.common.data.Cursor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : ClubQueryService")
@ExtendWith(MockitoExtension.class)
class ClubQueryServiceTest {

    @Mock
    private ClubQueryPort clubQueryPort;

    @InjectMocks
    private ClubQueryService clubQueryService;

    private List<ClubReadModel> mockReadModels =
            List.of(
                    new ClubReadModel(
                            1L,
                            "쿠링",
                            "건국대 공지사항 앱 만드는 개발 동아리",
                            "icon-url-1",
                            "ACADEMIC",
                            "CENTRAL",
                            LocalDateTime.of(2025, 3, 1, 0, 0),
                            LocalDateTime.of(2025, 3, 31, 23, 59)
                    ),
                    new ClubReadModel(
                            2L,
                            "쿠잇",
                            "건국대 개발 동아리",
                            "icon-url-2",
                            "ACADEMIC",
                            "ENGINEERING",
                            LocalDateTime.of(2025, 3, 1, 0, 0),
                            LocalDateTime.of(2025, 3, 31, 23, 59)
                    ),
                    new ClubReadModel(
                            3L,
                            "DIUS",
                            "건국대 공과대학 댄스 동아리",
                            "icon-url-3",
                            "CULTURE_ART",
                            "ENGINEERING",
                            LocalDateTime.of(2025, 2, 20, 0, 0),
                            LocalDateTime.of(2025, 3, 31, 23, 59)
                    )
            );

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

    @Test
    @DisplayName("동아리 목록을 정상적으로 조회한다")
    void getClubs_success() {

        // given
        String category = "academic";
        String divisions = "central,engineering";
        Cursor cursor = Cursor.from(null);
        int size = 10;
        String sortBy = "name";

        ClubListCommand command = new ClubListCommand(category, divisions, cursor, size, sortBy);

        List<String> divisionList = List.of("central", "engineering");

        when(clubQueryPort.searchClubs(category, divisionList, cursor, size + 1, sortBy))
                .thenReturn(mockReadModels);

        when(clubQueryPort.countClubs(category, divisionList))
                .thenReturn(2);

        // when
        ClubListResult result = clubQueryService.getClubs(command);

        // then
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.clubs()).hasSize(3);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.cursor()).isNull();

        verify(clubQueryPort).searchClubs(category, divisionList, cursor, size + 1, sortBy);
        verify(clubQueryPort).countClubs(category, divisionList);
    }

}

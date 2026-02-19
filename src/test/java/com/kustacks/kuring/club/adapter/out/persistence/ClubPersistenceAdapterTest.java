package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClubPersistenceAdapter")
class ClubPersistenceAdapterTest {

    @InjectMocks
    private ClubPersistenceAdapter adapter;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private ClubSubscribeRepository clubSubscribeRepository;

    @Mock
    private EntityManager entityManager;

    @DisplayName("내일 마감 동아리 조회는 [내일 00:00, 내일모레 00:00) 범위를 사용한다")
    @Test
    void find_tomorrow_recruit_end_clubs_with_expected_window() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 19, 18, 0, 0);
        when(clubRepository.findClubsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of());

        adapter.findNextDayRecruitEndClubs(now);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(clubRepository).findClubsBetweenDates(startCaptor.capture(), endCaptor.capture());

        assertThat(startCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 20, 0, 0, 0));
        assertThat(endCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 21, 0, 0, 0));
    }

    @DisplayName("기간 조회 메서드는 repository를 그대로 위임한다")
    @Test
    void find_clubs_between_dates_delegate_to_repository() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 20, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 21, 0, 0, 0);
        List<Club> expected = List.of();
        when(clubRepository.findClubsBetweenDates(start, end)).thenReturn(expected);

        List<Club> actual = adapter.findClubsBetweenDates(start, end);

        assertThat(actual).isEqualTo(expected);
        verify(clubRepository).findClubsBetweenDates(start, end);
    }
}

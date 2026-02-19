package com.kustacks.kuring.club.adapter.out.persistence;

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


    @DisplayName("내일 마감 동아리 조회는 [내일 00:00, 내일모레 00:00) 범위를 사용한다")
    @Test
    void find_tomorrow_recruit_end_clubs_with_expected_window() {
        //given
        LocalDateTime now = LocalDateTime.of(2026, 2, 19, 18, 0, 0);
        when(clubRepository.findClubsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of());

        //when
        adapter.findNextDayRecruitEndClubs(now);

        //then
        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(clubRepository).findClubsBetweenDates(startCaptor.capture(), endCaptor.capture());

        assertThat(startCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 20, 0, 0, 0));
        assertThat(endCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 21, 0, 0, 0));
    }
}

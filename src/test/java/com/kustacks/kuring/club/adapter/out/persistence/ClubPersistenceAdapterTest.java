package com.kustacks.kuring.club.adapter.out.persistence;

import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.club.domain.ClubSubscribe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

        assertAll(
                () -> assertThat(startCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 20, 0, 0, 0)),
                () -> assertThat(endCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 2, 21, 0, 0, 0))
        );
    }

    @Test
    @DisplayName("countSubscribersByClubIds는 Object[]를 Map<Long, Long>으로 변환한다")
    void countSubscribersByClubIds_mapping_success() {
        //given
        List<Long> clubIds = List.of(1L, 2L);

        when(clubSubscribeRepository.countSubscribersByClubIds(clubIds))
                .thenReturn(List.of(
                        new Object[]{1L, 5L},
                        new Object[]{2L, 3L}
                ));

        //when
        Map<Long, Long> result = adapter.countSubscribersByClubIds(clubIds);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).isEqualTo(5L);
        assertThat(result.get(2L)).isEqualTo(3L);

        verify(clubSubscribeRepository).countSubscribersByClubIds(clubIds);
    }

    @Test
    @DisplayName("countSubscribersByClubIds는 null 또는 빈 리스트면 빈 Map을 반환한다")
    void countSubscribersByClubIds_empty_input() {

        // given
        List<Long> emptyList = List.of();

        // when
        Map<Long, Long> result_null = adapter.countSubscribersByClubIds(null);

        Map<Long, Long> result_empty = adapter.countSubscribersByClubIds(emptyList);

        // then
        assertThat(result_null).isEmpty();
        assertThat(result_empty).isEmpty();

        verify(clubSubscribeRepository, never()).countSubscribersByClubIds(any());
    }

    @Test
    @DisplayName("findSubscribedClubIds는 구독된 clubId 목록을 반환한다")
    void findSubscribedClubIds_success() {
        // given
        Long rootUserId = 100L;
        List<Long> clubIds = List.of(1L, 2L);

        Club club1 = mock(Club.class);
        Club club2 = mock(Club.class);

        when(club1.getId()).thenReturn(1L);
        when(club2.getId()).thenReturn(2L);

        ClubSubscribe subscribe1 = mock(ClubSubscribe.class);
        ClubSubscribe subscribe2 = mock(ClubSubscribe.class);

        when(subscribe1.getClub()).thenReturn(club1);
        when(subscribe2.getClub()).thenReturn(club2);

        when(clubSubscribeRepository
                .findByClubIdInAndRootUserId(clubIds, rootUserId))
                .thenReturn(List.of(subscribe1, subscribe2));

        // when
        List<Long> result = adapter.findSubscribedClubIds(clubIds, rootUserId);

        // then
        assertThat(result).containsExactly(1L, 2L);

        verify(clubSubscribeRepository).findByClubIdInAndRootUserId(clubIds, rootUserId);
    }
    
}

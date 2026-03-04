package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.dto.ClubDetailCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubDetailResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubDivisionResult;
import com.kustacks.kuring.club.application.port.in.dto.ClubListCommand;
import com.kustacks.kuring.club.application.port.in.dto.ClubListResult;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.application.port.out.dto.ClubDetailReadModel;
import com.kustacks.kuring.club.application.port.out.dto.ClubReadModel;
import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.kustacks.kuring.club.domain.ClubRecruitmentStatus;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : ClubQueryService")
@ExtendWith(MockitoExtension.class)
class ClubQueryServiceTest {

    @Mock
    private ClubQueryPort clubQueryPort;

    @Mock
    private ClubSubscriptionQueryPort clubSubscriptionQueryPort;

    @Mock
    private RootUserQueryPort rootUserQueryPort;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private ClubQueryService clubQueryService;

    private List<ClubReadModel> mockReadModels =
            List.of(
                    new ClubReadModel(
                            1L,
                            "쿠링",
                            "건국대 공지사항 앱 만드는 개발 동아리",
                            "icon-url-1",
                            ClubCategory.ACADEMIC,
                            ClubDivision.CENTRAL,
                            LocalDateTime.of(2026, 3, 1, 0, 0),
                            LocalDateTime.of(2026, 3, 31, 23, 59)
                    ),
                    new ClubReadModel(
                            2L,
                            "쿠잇",
                            "건국대 개발 동아리",
                            "icon-url-2",
                            ClubCategory.ACADEMIC,
                            ClubDivision.ENGINEERING,
                            LocalDateTime.of(2026, 3, 1, 0, 0),
                            LocalDateTime.of(2026, 3, 31, 23, 59)
                    ),
                    new ClubReadModel(
                            3L,
                            "DIUS",
                            "건국대 공과대학 댄스 동아리",
                            "icon-url-3",
                            ClubCategory.CULTURE_ART,
                            ClubDivision.ENGINEERING,
                            LocalDateTime.of(2026, 2, 10, 0, 0),
                            LocalDateTime.of(2026, 2, 25, 23, 59)
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

        String email = "test@test.com";
        Long rootUserId = 100L;

        RootUser rootUser = mock(RootUser.class);
        when(rootUser.getId()).thenReturn(rootUserId);
        when(rootUserQueryPort.findRootUserByEmail(email))
                .thenReturn(Optional.of(rootUser));

        ClubListCommand command = new ClubListCommand(category, divisions, email);

        List<ClubDivision> divisionList = List.of(ClubDivision.CENTRAL, ClubDivision.ENGINEERING);

        when(clubQueryPort.searchClubs(
                ClubCategory.ACADEMIC,
                divisionList
        )).thenReturn(mockReadModels);

        when(clubSubscriptionQueryPort.countSubscribersByClubIds(any()))
                .thenReturn(Map.of(
                        1L, 10L,
                        2L, 10L,
                        3L, 10L
                ));

        when(clubSubscriptionQueryPort.findSubscribedClubIds(any(), anyLong()))
                .thenReturn(List.of(1L, 2L, 3L));

        when(storagePort.getPresignedUrl(any()))
                .thenReturn("presigned-icon-url");

        // when
        ClubListResult result = clubQueryService.getClubs(command);

        // then
        assertThat(result.clubs()).hasSize(3);
        assertThat(result.clubs().get(0).subscriberCount()).isEqualTo(10);
        assertThat(result.clubs().get(0).isSubscribed()).isTrue();

        verify(rootUserQueryPort).findRootUserByEmail(email);
        verify(clubQueryPort).searchClubs(
                ClubCategory.ACADEMIC,
                divisionList
        );
        verify(storagePort, times(3)).getPresignedUrl(any());
    }

    @Test
    @DisplayName("비로그인 사용자는 목록에서 구독 여부를 조회하지 않는다")
    void getClubs_withoutLogin() {
        //given
        String category = "academic";
        String divisions = "central,engineering";
        String email = null;

        ClubListCommand command = new ClubListCommand(category, divisions, email);

        List<ClubDivision> divisionList = List.of(ClubDivision.CENTRAL, ClubDivision.ENGINEERING);

        when(clubQueryPort.searchClubs(
                ClubCategory.ACADEMIC,
                divisionList
        )).thenReturn(mockReadModels);

        when(clubSubscriptionQueryPort.countSubscribersByClubIds(any()))
                .thenReturn(Map.of(
                        1L, 5L,
                        2L, 5L,
                        3L, 5L
                ));

        //when
        ClubListResult result = clubQueryService.getClubs(command);

        //then
        assertThat(result.clubs().get(0).isSubscribed()).isFalse();
        verify(clubSubscriptionQueryPort, never()).findSubscribedClubIds(any(), anyLong());
    }

    @Test
    @DisplayName("동아리 상세 정보를 정상적으로 조회한다")
    void getClubDetail_success() {
        // given
        Long clubId = 1L;

        String email = "test@test.com";
        Long rootUserId = 100L;

        ClubDetailCommand command = new ClubDetailCommand(clubId, email);

        RootUser rootUser = mock(RootUser.class);
        when(rootUser.getId()).thenReturn(rootUserId);
        when(rootUserQueryPort.findRootUserByEmail(email))
                .thenReturn(Optional.of(rootUser));

        ClubDetailReadModel clubDetailReadModel = new ClubDetailReadModel(
                1L,
                "쿠링",
                "건국대 공지사항 앱 만드는 개발 동아리",
                ClubCategory.ACADEMIC,
                ClubDivision.CENTRAL,
                "instagram-url",
                "youtube-url",
                null,
                "상세 설명",
                "지원 자격",
                LocalDateTime.of(2099, 3, 1, 0, 0),
                LocalDateTime.of(2099, 3, 31, 23, 59),
                false,
                "apply-url",
                "poster-path",
                "공학관",
                "101호",
                127.0,
                37.5
        );

        when(clubQueryPort.findClubDetailById(clubId))
                .thenReturn(Optional.of(clubDetailReadModel));

        when(clubSubscriptionQueryPort.countSubscribers(clubId))
                .thenReturn(10L);

        when(clubSubscriptionQueryPort.existsSubscription(rootUserId, clubId))
                .thenReturn(true);

        when(storagePort.getPresignedUrl("poster-path"))
                .thenReturn("presigned-poster-url");

        // when
        ClubDetailResult result = clubQueryService.getClubDetail(command);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("쿠링");
        assertThat(result.subscriberCount()).isEqualTo(10);
        assertThat(result.isSubscribed()).isTrue();
        assertThat(result.location().building()).isEqualTo("공학관");
        assertThat(result.recruitmentStatus())
                .isEqualTo(ClubRecruitmentStatus.BEFORE);
        assertThat(result.category())
                .isEqualTo(ClubCategory.ACADEMIC);
        assertThat(result.division())
                .isEqualTo(ClubDivision.CENTRAL);
        assertThat(result.posterImageUrl())
                .isEqualTo("presigned-poster-url");

        verify(rootUserQueryPort).findRootUserByEmail(email);
        verify(clubQueryPort).findClubDetailById(clubId);
        verify(clubSubscriptionQueryPort).countSubscribers(clubId);
        verify(clubSubscriptionQueryPort).existsSubscription(rootUserId, clubId);
        verify(storagePort, times(1)).getPresignedUrl("poster-path");
    }

    @Test
    @DisplayName("비로그인 사용자는 구독 여부를 조회하지 않는다")
    void getClubDetail_withoutLogin() {

        Long clubId = 1L;
        String email = null;

        ClubDetailCommand command = new ClubDetailCommand(clubId, email);

        ClubDetailReadModel dto = new ClubDetailReadModel(
                1L, "쿠링", "건국대 공지사항 앱 만드는 개발 동아리",
                ClubCategory.ACADEMIC, ClubDivision.CENTRAL,
                null, null, null,
                null, null,
                null, null,
                true,
                null, null,
                null, null,
                null, null
        );

        when(clubQueryPort.findClubDetailById(clubId))
                .thenReturn(Optional.of(dto));

        when(clubSubscriptionQueryPort.countSubscribers(clubId))
                .thenReturn(5L);

        // when
        ClubDetailResult result = clubQueryService.getClubDetail(command);

        // then
        assertThat(result.isSubscribed()).isFalse();
        verify(clubSubscriptionQueryPort).countSubscribers(clubId);
        verify(clubSubscriptionQueryPort, never()).existsSubscription(anyLong(), any());
        assertThat(result.subscriberCount()).isEqualTo(5);
    }

    @DisplayName("상세 조회 시 posterImagePath가 있으면 Presigned URL로 변환한다")
    @Test
    void getClubDetail_convert_posterImagePath_to_presignedUrl() {
        // given
        Long clubId = 1L;
        String key = "club/poster/test.png";
        String presignedUrl = "https://presigned-url";

        ClubDetailReadModel readModel = mock(ClubDetailReadModel.class);

        when(readModel.getId()).thenReturn(clubId);
        when(readModel.getName()).thenReturn("쿠링");
        when(readModel.getSummary()).thenReturn("건국대 공지사항 앱 만드는 개발 동아리");
        when(readModel.getCategory()).thenReturn(ClubCategory.ACADEMIC);
        when(readModel.getDivision()).thenReturn(ClubDivision.CENTRAL);
        when(readModel.getRecruitStartAt()).thenReturn(LocalDateTime.now().minusDays(1));
        when(readModel.getRecruitEndAt()).thenReturn(LocalDateTime.now().plusDays(1));
        when(readModel.getIsAlways()).thenReturn(false);
        when(readModel.getPosterImagePath()).thenReturn(key);
        when(readModel.hasLocation()).thenReturn(false);

        when(clubQueryPort.findClubDetailById(clubId)).thenReturn(Optional.of(readModel));

        when(clubSubscriptionQueryPort.countSubscribers(clubId)).thenReturn(0L);

        when(storagePort.getPresignedUrl(key)).thenReturn(presignedUrl);

        ClubDetailCommand command = new ClubDetailCommand(clubId, null);

        // when
        var result = clubQueryService.getClubDetail(command);

        // then
        assertThat(result.posterImageUrl()).isEqualTo(presignedUrl);
        verify(storagePort, times(1)).getPresignedUrl(key);
    }

}

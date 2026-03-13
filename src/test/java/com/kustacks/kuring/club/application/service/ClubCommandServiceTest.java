package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.dto.*;
import com.kustacks.kuring.club.application.port.out.*;
import com.kustacks.kuring.club.application.port.out.dto.*;
import com.kustacks.kuring.club.domain.*;
import com.kustacks.kuring.common.exception.*;
import com.kustacks.kuring.common.exception.code.*;
import com.kustacks.kuring.common.properties.*;
import com.kustacks.kuring.user.application.port.out.*;
import com.kustacks.kuring.user.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.test.util.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClubSubscriptionCommandService")
class ClubCommandServiceTest {

    @InjectMocks
    private ClubCommandService service;

    @Mock
    private ClubQueryPort clubQueryPort;

    @Mock
    private ServerProperties serverProperties;

    @Mock
    private ClubSubscriptionCommandPort clubSubscriptionCommandPort;

    @Mock
    private ClubSubscriptionQueryPort clubSubscriptionQueryPort;

    @Mock
    private RootUserQueryPort rootUserQueryPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserEventPort userEventPort;

    private RootUser rootUser;

    private Club club;

    @BeforeEach
    void setUp() {
        rootUser = rootUser();
        club = club();
    }

    @DisplayName("구독 추가 성공")
    @Test
    void add_subscription_success() {
        //given
        User user1 = new User("token-1");
        User user2 = new User("token-2");

        when(club.getId()).thenReturn(1L);
        when(serverProperties.ifDevThenAddSuffix(anyString())).thenReturn("club.1");
        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr"))
                .thenReturn(Optional.of(rootUser));
        when(clubQueryPort.findClubById(1L)).thenReturn(Optional.of(club));
        when(userQueryPort.findByLoggedInUserId(1L)).thenReturn(List.of(user1, user2));
        when(clubSubscriptionQueryPort.countSubscriptions(1L)).thenReturn(1L);

        //when
        long count = service.addSubscription(new ClubSubscriptionCommand("client@konkuk.ac.kr", 1L));

        //then
        assertAll(
                () -> assertThat(count).isEqualTo(1L),
                () -> verify(userEventPort).subscribeEvent("token-1", "club.1"),
                () -> verify(userEventPort).subscribeEvent("token-2", "club.1")
        );
    }

    @DisplayName("이미 구독된 동아리는 추가할 수 없다")
    @Test
    void add_subscription_fail_when_already_subscribed() {
        //given
        when(club.getId()).thenReturn(1L);
        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr"))
                .thenReturn(Optional.of(rootUser));
        when(clubQueryPort.findClubById(1L)).thenReturn(Optional.of(club));
        when(clubSubscriptionQueryPort.existsSubscription(1L, 1L)).thenReturn(Boolean.TRUE);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> service.addSubscription(new ClubSubscriptionCommand("client@konkuk.ac.kr", 1L)))
                        .isInstanceOf(InvalidStateException.class)
                        .extracting(ex -> ((InvalidStateException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CLUB_ALREADY_SUBSCRIBED),
                () -> verify(userEventPort, never()).subscribeEvent(anyString(), anyString())
        );
    }

    @DisplayName("구독 제거 성공")
    @Test
    void remove_subscription_success() {
        //given
        User user1 = new User("token-1");

        when(club.getId()).thenReturn(1L);
        when(serverProperties.ifDevThenAddSuffix(anyString())).thenReturn("club.1");
        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr"))
                .thenReturn(Optional.of(rootUser));
        when(clubQueryPort.findClubById(1L)).thenReturn(Optional.of(club));
        when(clubSubscriptionQueryPort.existsSubscription(1L, club.getId())).thenReturn(Boolean.TRUE);
        when(userQueryPort.findByLoggedInUserId(1L)).thenReturn(List.of(user1));

        //when
        long count = service.removeSubscription(new ClubSubscriptionCommand("client@konkuk.ac.kr", 1L));

        //then
        assertAll(
                () -> assertThat(count).isEqualTo(0L),
                () -> verify(userEventPort).unsubscribeEvent("token-1", "club.1")
        );
    }

    @DisplayName("구독하지 않은 동아리는 제거할 수 없다")
    @Test
    void remove_subscription_fail_when_not_subscribed() {
        //given
        when(club.getId()).thenReturn(1L);
        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr"))
                .thenReturn(Optional.of(rootUser));
        when(clubQueryPort.findClubById(1L)).thenReturn(Optional.of(club));
        when(clubSubscriptionQueryPort.existsSubscription(1L, club.getId())).thenReturn(Boolean.FALSE);

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> service.removeSubscription(new ClubSubscriptionCommand("client@konkuk.ac.kr", 1L)))
                        .isInstanceOf(InvalidStateException.class)
                        .extracting(ex -> ((InvalidStateException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CLUB_NOT_SUBSCRIBED),
                () -> verify(userEventPort, never()).unsubscribeEvent(anyString(), anyString())
        );
    }

    @DisplayName("존재하지 않는 동아리는 구독할 수 없다")
    @Test
    void add_subscription_fail_when_club_not_found() {
        //given
        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr"))
                .thenReturn(Optional.of(rootUser));
        when(clubQueryPort.findClubById(1L)).thenReturn(Optional.empty());

        //when & then
        assertAll(
                () -> assertThatThrownBy(() -> service.addSubscription(new ClubSubscriptionCommand("client@konkuk.ac.kr", 1L)))
                        .isInstanceOf(InvalidStateException.class)
                        .extracting(ex -> ((InvalidStateException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CLUB_NOT_FOUND),
                () -> verify(userEventPort, never()).subscribeEvent(anyString(), anyString())
        );
    }


    @DisplayName("구독한 동아리 목록 조회 성공")
    @Test
    void get_subscribed_clubs_success() {
        // given
        ClubReadModel readModel = new ClubReadModel(
                1L,
                "쿠링",
                "건국대 공지사항 앱 만드는 개발 동아리",
                null,
                com.kustacks.kuring.club.domain.ClubCategory.ACADEMIC,
                com.kustacks.kuring.club.domain.ClubDivision.CENTRAL,
                null,
                null
        );

        when(rootUserQueryPort.findRootUserByEmail("client@konkuk.ac.kr")).thenReturn(Optional.of(rootUser));
        when(clubSubscriptionQueryPort.findSubscribedClubIdsByRootUserId(1L)).thenReturn(List.of(1L));
        when(clubQueryPort.findClubReadModelsByIds(List.of(1L))).thenReturn(List.of(readModel));
        when(clubSubscriptionQueryPort.countSubscribersByClubIds(List.of(1L))).thenReturn(Map.of(1L, 5L));

        // when
        ClubListResult result = service.getSubscribedClubs(new SubscribedClubListCommand("client@konkuk.ac.kr"));

        // then
        assertAll(
                () -> assertThat(result.clubs()).hasSize(1),
                () -> assertThat(result.clubs().get(0).id()).isEqualTo(1L),
                () -> assertThat(result.clubs().get(0).name()).isEqualTo("쿠링"),
                () -> assertThat(result.clubs().get(0).isSubscribed()).isTrue(),
                () -> assertThat(result.clubs().get(0).subscriberCount()).isEqualTo(5L)
        );
    }

    private RootUser rootUser() {
        RootUser rootUser = new RootUser("client@konkuk.ac.kr", "password", "nickname");
        ReflectionTestUtils.setField(rootUser, "id", 1L);
        return rootUser;
    }

    private Club club() {
        return mock(Club.class);
    }
}

package com.kustacks.kuring.club.application.service;

import com.kustacks.kuring.club.application.port.in.dto.ClubSubscriptionCommand;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionCommandPort;
import com.kustacks.kuring.club.application.port.out.ClubSubscriptionQueryPort;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClubSubscriptionCommandService")
class ClubCommandServiceTest {

    @InjectMocks
    private ClubCommandService service;

    @Mock
    private ClubQueryPort clubQueryPort;

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

    private RootUser rootUser() {
        RootUser rootUser = new RootUser("client@konkuk.ac.kr", "password", "nickname");
        ReflectionTestUtils.setField(rootUser, "id", 1L);
        return rootUser;
    }

    private Club club() {
        return mock(Club.class);
    }
}

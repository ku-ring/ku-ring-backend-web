package com.kustacks.kuring.club.application.service;

import com.google.firebase.messaging.Message;
import com.kustacks.kuring.club.application.port.out.ClubQueryPort;
import com.kustacks.kuring.club.domain.Club;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClubNotificationService")
class ClubNotificationServiceTest {

    @Mock
    private ClubQueryPort clubQueryPort;

    @Mock
    private FirebaseMessagingPort firebaseMessagingPort;

    @DisplayName("내일 마감 대상 목록을 모두 발송한다")
    @Test
    void send_deadline_notifications_only_for_targets() throws Exception {
        Club target = club(1L, "쿠링");
        Club target2 = club(2L, "리드미");

        when(clubQueryPort.findNextDayRecruitEndClubs(any(LocalDateTime.class)))
                .thenReturn(List.of(target, target2));

        ClubNotificationService service = new ClubNotificationService(clubQueryPort, firebaseMessagingPort, new ServerProperties("dev"));
        service.sendDeadlineNotifications();

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessagingPort, times(2)).send(captor.capture());

        Message sent = captor.getAllValues().get(0);
        String topic = (String) ReflectionTestUtils.getField(sent, "topic");
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) ReflectionTestUtils.getField(sent, "data");
        assertThat(topic).isEqualTo("club.1.dev");
        assertThat(data).containsEntry("clubId", "1");
        assertThat(data).containsEntry("messageType", "club");
    }

    @DisplayName("내일 마감 대상은 adapter 전용 메서드로 조회한다")
    @Test
    void should_query_by_adapter_method() {
        when(clubQueryPort.findNextDayRecruitEndClubs(any(LocalDateTime.class)))
                .thenReturn(List.of());

        ClubNotificationService service = new ClubNotificationService(clubQueryPort, firebaseMessagingPort, new ServerProperties("dev"));
        service.sendDeadlineNotifications();

        verify(clubQueryPort).findNextDayRecruitEndClubs(any(LocalDateTime.class));
    }

    @DisplayName("발송 실패가 발생해도 다른 대상은 계속 발송")
    @Test
    void should_continue_even_if_one_send_fails() throws Exception {
        Club first = club(1L, "첫번째");
        Club second = club(2L, "두번째");

        when(clubQueryPort.findNextDayRecruitEndClubs(any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));
        doThrow(new RuntimeException("send fail"))
                .doNothing()
                .when(firebaseMessagingPort)
                .send(any(Message.class));

        ClubNotificationService service = new ClubNotificationService(clubQueryPort, firebaseMessagingPort, new ServerProperties("dev"));
        service.sendDeadlineNotifications();

        verify(firebaseMessagingPort, times(2)).send(any(Message.class));
    }

    private Club club(Long id, String name) {
        Club club = mock(Club.class);
        when(club.getId()).thenReturn(id);
        when(club.getName()).thenReturn(name);
        return club;
    }
}

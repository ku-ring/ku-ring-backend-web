package com.kustacks.kuring.calendar.application.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("서비스 : AcademicEventNotificationService")
class AcademicEventNotificationServiceTest {

    @Mock
    private AcademicEventQueryPort academicEventQueryPort;

    @Mock
    private FirebaseMessagingPort firebaseMessagingPort;

    @Mock
    private ServerProperties serverProperties;

    @InjectMocks
    private AcademicEventNotificationService service;

    @DisplayName("오늘 일정이 없으면 알림을 발송하지 않는다")
    @Test
    void should_not_send_notification_when_no_events() throws FirebaseMessagingException {
        // given
        when(academicEventQueryPort.findTodayEvents(any(LocalDate.class))).thenReturn(List.of());

        // when
        service.sendTodayAcademicEventNotifications();

        // then
        verify(firebaseMessagingPort, never()).send(any(Message.class));
    }

    @DisplayName("일정이 있으면 토픽으로 알림을 발송한다")
    @Test
    void should_send_notification_to_topic_when_events_exist() throws FirebaseMessagingException {
        // given
        AcademicEventReadModel event = createTestEventReadModel(1L, "개강", LocalDateTime.now(), LocalDateTime.now());
        when(academicEventQueryPort.findTodayEvents(any(LocalDate.class))).thenReturn(List.of(event));
        when(serverProperties.ifDevThenAddSuffix("academicEvent")).thenReturn("academicEvent");

        // when
        service.sendTodayAcademicEventNotifications();

        // then
        verify(firebaseMessagingPort, times(1)).send(any(Message.class));
    }

    @DisplayName("여러 일정이 있으면 각 일정마다 토픽으로 알림을 발송한다")
    @Test
    void should_send_notifications_for_multiple_events() throws FirebaseMessagingException {
        // given
        AcademicEventReadModel event1 = createTestEventReadModel(1L, "개강", LocalDateTime.now(), LocalDateTime.now());
        AcademicEventReadModel event2 = createTestEventReadModel(2L, "종강", LocalDateTime.now(), LocalDateTime.now());
        when(academicEventQueryPort.findTodayEvents(any(LocalDate.class))).thenReturn(List.of(event1, event2));
        when(serverProperties.ifDevThenAddSuffix("academicEvent")).thenReturn("academicEvent");

        // when
        service.sendTodayAcademicEventNotifications();

        // then
        verify(firebaseMessagingPort, times(2)).send(any(Message.class));
    }

    private AcademicEventReadModel createTestEventReadModel(Long id, String summary, LocalDateTime startTime, LocalDateTime endTime) {
        return new AcademicEventReadModel(
                id,
                UUID.randomUUID().toString(),
                summary,
                "테스트 이벤트",
                "TEST",
                Transparent.TRANSPARENT,
                1,
                true,
                startTime,
                endTime
        );
    }
}
package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@EnableAsync
class NewMessageEventListenerTest {

    @MockBean
    private HandleMessageEventUseCase handleMessageEventUseCase;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    @DisplayName("AdminNotificationEvent를 수신하면 handle에 위임한다")
    void sendAdminNotification_success() {
        // given
        AdminNotificationEvent event = new AdminNotificationEvent("title", "body", "url");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("AdminTestNotificationEvent를 수신하면 handle에 위임한다")
    void sendAdminTestNotification_success() {
        // given
        AdminTestNotificationEvent event = new AdminTestNotificationEvent(
                "1","articleId", "2026-03-22", "category", "subject", "카테고리", "baseUrl"
        );
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("AlertSendEvent를 수신하면 handle에 위임한다")
    void sendAlert_success() {
        // given
        AlertSendEvent event = new AlertSendEvent("title", "content");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("AcademicTestNotificationEvent를 수신하면 handle에 위임한다")
    void sendAcademicTestNotification_success() {
        // given
        AcademicTestNotificationEvent event = new AcademicTestNotificationEvent("title", "body");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("AcademicScheduleNotificationEvent를 수신하면 handle에 위임한다")
    void sendAcademicScheduleNotification_success() {
        // given
        AcademicScheduleNotificationEvent event = new AcademicScheduleNotificationEvent("수강신청");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("ClubDeadlineNotificationEvent를 수신하면 handle에 위임한다")
    void sendClubDeadlineNotification_success() {
        //given
        ClubDeadlineNotificationEvent event = new ClubDeadlineNotificationEvent(1L, "KUSTACKS");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

    @Test
    @DisplayName("NoticeBatchNotificationEvent를 수신하면 handle에 위임한다")
    void sendNoticeBatchNotification_success() {
        // given
        NoticeBatchNotificationEvent event = new NoticeBatchNotificationEvent(
                List.of(new NoticeBatchNotificationEvent.NoticeMessageDto(
                        "articleId",
                        "2026-03-24",
                        "subject",
                        "category",
                        "카테고리",
                        "baseUrl"
                ))
        );

        // when
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        applicationEventPublisher.publishEvent(event);

        // then
        verify(handleMessageEventUseCase, timeout(1000)).handle(event);
    }

}
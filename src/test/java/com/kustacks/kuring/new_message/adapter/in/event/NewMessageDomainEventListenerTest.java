package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewMessageDomainEventListenerTest {

    @Mock
    private HandleMessageEventUseCase handleMessageEventUseCase;

    @InjectMocks
    private NewMessageDomainEventListener newMessageDomainEventListener;

    @Test
    @DisplayName("AcademicScheduleNotificationEvent를 수신하면 handle에 위임한다")
    void sendAcademicScheduleNotification_success() {
        // given
        AcademicScheduleNotificationEvent event = new AcademicScheduleNotificationEvent("수강신청");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageDomainEventListener.sendAcademicScheduleNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

    @Test
    @DisplayName("ClubDeadlineNotificationEvent를 수신하면 handle에 위임한다")
    void sendClubDeadlineNotification_success() {
        // given
        ClubDeadlineNotificationEvent event = new ClubDeadlineNotificationEvent(1L, "KUSTACKS");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageDomainEventListener.sendClubDeadlineNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
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
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageDomainEventListener.sendNoticeBatchNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

}

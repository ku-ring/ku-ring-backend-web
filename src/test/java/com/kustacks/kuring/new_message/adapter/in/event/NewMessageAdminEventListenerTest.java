package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewMessageAdminEventListenerTest {

    @Mock
    private HandleMessageEventUseCase handleMessageEventUseCase;

    @InjectMocks
    private NewMessageAdminEventListener newMessageAdminEventListener;

    @Test
    @DisplayName("AdminNotificationEvent를 수신하면 handle에 위임한다")
    void sendAdminNotification_success() {
        // given
        AdminNotificationEvent event = new AdminNotificationEvent("title", "body", "url");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageAdminEventListener.sendAdminNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

    @Test
    @DisplayName("AdminTestNotificationEvent를 수신하면 handle에 위임한다")
    void sendAdminTestNotification_success() {
        // given
        AdminTestNotificationEvent event = new AdminTestNotificationEvent(
                "articleId", "2026-03-22", "category", "subject", "카테고리", "baseUrl"
        );
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageAdminEventListener.sendAdminTestNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

    @Test
    @DisplayName("AlertSendEvent를 수신하면 handle에 위임한다")
    void sendAlert_success() {
        // given
        AlertSendEvent event = new AlertSendEvent("title", "content");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageAdminEventListener.sendAlert(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

    @Test
    @DisplayName("AcademicTestNotificationEvent를 수신하면 handle에 위임한다")
    void sendAcademicTestNotification_success() {
        // given
        AcademicTestNotificationEvent event = new AcademicTestNotificationEvent("title", "body");
        when(handleMessageEventUseCase.handle(event)).thenReturn(1);

        // when
        newMessageAdminEventListener.sendAcademicTestNotification(event);

        // then
        verify(handleMessageEventUseCase).handle(event);
    }

    @Test
    @DisplayName("recover 메서드는 예외 없이 종료된다")
    void recover_methods() {
        // given
        Exception exception = new RuntimeException();

        // when & then
        assertAll(
                () -> assertDoesNotThrow(() ->
                        newMessageAdminEventListener.recoverAdminNotification(
                                exception,
                                new AdminNotificationEvent("title", "body", "url")
                        )
                ),

                () -> assertDoesNotThrow(() ->
                        newMessageAdminEventListener.recoverAdminTestNotification(
                                exception,
                                new AdminTestNotificationEvent(
                                        "articleId",
                                        "2026-03-24",
                                        "category",
                                        "subject",
                                        "카테고리",
                                        "baseUrl"
                                )
                        )
                ),

                () -> assertDoesNotThrow(() ->
                        newMessageAdminEventListener.recoverAlert(
                                exception,
                                new AlertSendEvent("title", "content")
                        )
                ),

                () -> assertDoesNotThrow(() ->
                        newMessageAdminEventListener.recoverAcademicTestNotification(
                                exception,
                                new AcademicTestNotificationEvent("title", "body")
                        )
                )
        );
    }
}
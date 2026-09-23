package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import com.kustacks.kuring.new_message.exception.message.MessageSendException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSendServiceTest {

    @Mock
    private PushMessagePort pushMessagePort;

    @InjectMocks
    private NotificationSendService notificationSendService;

    @Test
    @DisplayName("알림 전송에 성공하면 true를 반환한다")
    void send_success() {
        // given
        NotificationCommand command = command("topic1", "title1", "body1");

        // when
        boolean result = notificationSendService.send(command);

        // then
        assertAll(
                () -> assertTrue(result),
                () -> verify(pushMessagePort).send(command)
        );
    }

    @Test
    @DisplayName("알림 전송 중 예외가 발생하면 false를 반환한다")
    void send_fail() {
        // given
        NotificationCommand command = command("topic1", "title1", "body1");
        doThrow(new MessageSendException()).when(pushMessagePort).send(command);

        // when
        boolean result = notificationSendService.send(command);

        // then
        assertAll(
                () -> assertFalse(result),
                () -> verify(pushMessagePort).send(command)
        );
    }

    @Test
    @DisplayName("여러 알림 전송 시 성공한 개수만 반환한다")
    void sendAll_countOnlySuccess() {
        // given
        NotificationCommand command1 = command("topic1", "title1", "body1");
        NotificationCommand command2 = command("topic2", "title2", "body2");
        NotificationCommand command3 = command("topic3", "title3", "body3");

        // pushMessagePort 성공/실패 예약 => 1.성공 2. 실패 3. 성공
        doNothing()
                .doThrow(new MessageSendException())
                .doNothing()
                .when(pushMessagePort).send(any(NotificationCommand.class));

        // when
        int result = notificationSendService.sendAll(List.of(command1, command2, command3));

        // then
        assertAll(
                () -> assertEquals(2, result),
                () -> verify(pushMessagePort, times(3)).send(any(NotificationCommand.class))
        );
    }

    private NotificationCommand command(String topic, String title, String body) {
        return new NotificationCommand(
                NotificationTarget.topic(topic),
                NotificationContent.of(title, body),
                MessageType.ADMIN,
                Map.of("key", "value")
        );
    }
}
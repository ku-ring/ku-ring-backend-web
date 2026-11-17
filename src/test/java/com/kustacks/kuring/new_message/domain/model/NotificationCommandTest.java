package com.kustacks.kuring.new_message.domain.model;

import com.kustacks.kuring.new_message.domain.enums.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationCommandTest {

    @Test
    @DisplayName("data가 null이면 빈 맵으로 초기화된다")
    void constructor_nullData() {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic"),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                null
        );

        // then
        assertTrue(command.data().isEmpty());
    }

    @Test
    @DisplayName("data는 방어적으로 복사된다")
    void constructor_copyData() {
        // given
        Map<String, String> source = new HashMap<>();
        source.put("key", "value");

        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic"),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                source
        );

        // when
        source.put("key", "changed");

        // then
        assertEquals("value", command.data().get("key"));
    }

    @Test
    @DisplayName("target, content, messageType 중 하나라도 null이면 예외가 발생한다")
    void constructor_fail_whenRequiredFieldIsNull() {
        // when
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new NotificationCommand(
                        null,
                        NotificationContent.of("title", "body"),
                        MessageType.ADMIN,
                        Map.of()
                )
        );

        // then
        assertEquals("target, content, messageType은 필수입니다.", exception.getMessage());
    }
}

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
    @DisplayName("mergedData는 title, body, messageType이 없으면 추가한다")
    void mergedData_putIfAbsent() {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic"),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                Map.of("custom", "value")
        );

        // when
        Map<String, String> merged = command.mergedData();

        // then
        assertAll(
                () -> assertEquals("value", merged.get("custom")),
                () -> assertEquals("title", merged.get("title")),
                () -> assertEquals("body", merged.get("body")),
                () -> assertEquals("admin", merged.get("type"))
        );
    }

    @Test
    @DisplayName("mergedData는 따로 명시된 title, body, messageType 값을 우선한다.")
    void mergedData_keepExistingValues() {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic"),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                Map.of(
                        "title", "title-changed",
                        "body", "body-changed",
                        "messageType", "type-changed"
                )
        );

        // when
        Map<String, String> merged = command.mergedData();

        // then
        assertAll(
                () -> assertEquals("title-changed", merged.get("title")),
                () -> assertEquals("body-changed", merged.get("body")),
                () -> assertEquals("type-changed", merged.get("messageType"))
        );
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

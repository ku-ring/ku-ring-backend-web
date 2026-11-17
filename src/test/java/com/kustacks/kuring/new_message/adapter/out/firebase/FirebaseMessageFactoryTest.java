package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FirebaseMessageFactory")
class FirebaseMessageFactoryTest {

    @Test
    @DisplayName("create는 topic, notification, data, aps를 포함한 메시지를 생성한다")
    void create_success() {
        // given
        String topic = "notice";
        String type = "admin";
        String title = "새 공지";
        String body = "공지 내용";
        Map<String, String> extraData = Map.of("url", "/notice/1");

        // when
        Message message = FirebaseMessageFactory.create(topic, type, title, body, extraData);
        CapturedMessage captured = extractMessage(message);

        // then
        assertAll(
                () -> assertEquals(topic, captured.topic()),
                () -> assertEquals(title, captured.title()),
                () -> assertEquals(body, captured.body()),
                () -> assertEquals("admin", captured.data().get("type")),
                () -> assertEquals(title, captured.data().get("title")),
                () -> assertEquals(body, captured.data().get("body")),
                () -> assertEquals("/notice/1", captured.data().get("url")),
                () -> assertEquals(1, captured.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("create는 extraData가 null이어도 기본 data를 포함한다")
    void create_withNullExtraData() {
        // given
        String topic = "notice";
        String type = "admin";
        String title = "title";
        String body = "body";

        // when
        Message message = FirebaseMessageFactory.create(topic, type, title, body, null);
        CapturedMessage captured = extractMessage(message);

        // then
        assertAll(
                () -> assertEquals(topic, captured.topic()),
                () -> assertEquals(title, captured.title()),
                () -> assertEquals(body, captured.body()),
                () -> assertEquals(type, captured.data().get("type")),
                () -> assertEquals(title, captured.data().get("title")),
                () -> assertEquals(body, captured.data().get("body")),
                () -> assertEquals(3, captured.data().size()),
                () -> assertEquals(1, captured.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("create는 extraData가 비어있어도 기본 data를 포함한다")
    void create_withEmptyExtraData() {
        // given
        String topic = "notice";
        String type = "admin";
        String title = "title";
        String body = "body";

        // when
        Message message = FirebaseMessageFactory.create(topic, type, title, body, Map.of());
        CapturedMessage captured = extractMessage(message);

        // then
        assertAll(
                () -> assertEquals(type, captured.data().get("type")),
                () -> assertEquals(title, captured.data().get("title")),
                () -> assertEquals(body, captured.data().get("body")),
                () -> assertEquals(3, captured.data().size())
        );
    }

    @Test
    @DisplayName("create는 extraData를 추가로 포함한다")
    void create_addExtraData() {
        // given
        String topic = "notice";
        String type = "admin";
        String title = "title";
        String body = "body";
        Map<String, String> extraData = Map.of(
                "url", "/notices/1",
                "image", "https://image.test/sample.png"
        );

        // when
        Message message = FirebaseMessageFactory.create(topic, type, title, body, extraData);
        CapturedMessage captured = extractMessage(message);

        // then
        assertAll(
                () -> assertEquals(type, captured.data().get("type")),
                () -> assertEquals(title, captured.data().get("title")),
                () -> assertEquals(body, captured.data().get("body")),
                () -> assertEquals("/notices/1", captured.data().get("url")),
                () -> assertEquals("https://image.test/sample.png", captured.data().get("image"))
        );
    }

    @Test
    @DisplayName("create는 extraData의 동일한 key가 있으면 extraData 값으로 덮어쓴다")
    void create_extraDataOverridesDefaultData() {
        // given
        String topic = "notice";
        String type = "admin";
        String title = "title";
        String body = "body";
        Map<String, String> extraData = Map.of(
                "type", "custom-type",
                "title", "custom-title",
                "body", "custom-body"
        );

        // when
        Message message = FirebaseMessageFactory.create(topic, type, title, body, extraData);
        CapturedMessage captured = extractMessage(message);

        // then
        assertAll(
                () -> assertEquals("custom-type", captured.data().get("type")),
                () -> assertEquals("custom-title", captured.data().get("title")),
                () -> assertEquals("custom-body", captured.data().get("body"))
        );
    }

    private record CapturedMessage(
            String topic,
            String title,
            String body,
            Map<String, String> data,
            Map<String, Object> aps
    ) {
    }

    private CapturedMessage extractMessage(Message message) {
        Object notification = ReflectionTestUtils.getField(message, "notification");

        Assertions.assertNotNull(notification);
        String topic = (String) ReflectionTestUtils.getField(message, "topic");
        String title = (String) ReflectionTestUtils.getField(notification, "title");
        String body = (String) ReflectionTestUtils.getField(notification, "body");

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) ReflectionTestUtils.getField(message, "data");

        @SuppressWarnings("unchecked")
        Map<String, Object> apnsPayload = (Map<String, Object>) ReflectionTestUtils.getField(
                ReflectionTestUtils.getField(message, "apnsConfig"),
                "payload"
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> aps = (Map<String, Object>) apnsPayload.get("aps");

        return new CapturedMessage(topic, title, body, data, aps);
    }
}
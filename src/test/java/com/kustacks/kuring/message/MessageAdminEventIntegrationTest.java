package com.kustacks.kuring.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.adapter.in.event.MessageAdminEventListener;
import com.kustacks.kuring.message.adapter.in.event.dto.AcademicTestNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AdminNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AdminTestNotificationEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.AlertSendEvent;
import com.kustacks.kuring.message.application.port.in.FirebaseWithAdminUseCase;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.service.FirebaseNotificationService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = MessageAdminEventIntegrationTest.TestConfig.class)
@DisplayName("MessageAdminEvent 전체 통합 테스트")
class MessageAdminEventIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(MessageAdminEventIntegrationTest.class);
    @Resource
    private MessageAdminEventListener messageAdminEventListener;

    @Resource
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        reset(firebaseMessaging);
    }

    @Configuration
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        FirebaseMessaging firebaseMessaging() {
            return mock(FirebaseMessaging.class);
        }

        @Bean
        ServerProperties serverProperties() {
            ServerProperties serverProperties = mock(ServerProperties.class);
            when(serverProperties.ifDevThenAddSuffix(anyString()))
                    .thenAnswer(invocation -> invocation.getArgument(0) + ".dev");
            when(serverProperties.addDevSuffix(anyString()))
                    .thenAnswer(invocation -> invocation.getArgument(0) + ".dev");
            return serverProperties;
        }

        @Bean
        FirebaseMessagingPort firebaseMessagingPort(FirebaseMessaging firebaseMessaging) {
            return firebaseMessaging::send;
        }

        @Bean
        FirebaseWithAdminUseCase firebaseWithAdminUseCase(
                FirebaseMessagingPort firebaseMessagingPort,
                ServerProperties serverProperties,
                ObjectMapper objectMapper
        ) {
            return new FirebaseNotificationService(firebaseMessagingPort, serverProperties, objectMapper);
        }

        @Bean
        MessageAdminEventListener messageAdminEventListener(FirebaseWithAdminUseCase firebaseWithAdminUseCase) {
            return new MessageAdminEventListener(firebaseWithAdminUseCase);
        }
    }

    @Test
    @DisplayName("AdminNotificationEvent 통합 테스트")
    void adminNotificationEvent_integration() throws Exception {
        // given
        AdminNotificationEvent event = new AdminNotificationEvent(
                "admin-title",
                "admin-body",
                "https://admin-url"
        );

        // when
        messageAdminEventListener.sendNotificationEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("admin-title", actual.title()),
                () -> assertEquals("admin-body", actual.body()),
                () -> assertEquals("admin-title", actual.data().get("title")),
                () -> assertEquals("admin-body", actual.data().get("body")),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("https://admin-url", actual.data().get("url"))
        );
    }

    @Test
    @DisplayName("AdminTestNotificationEvent 통합 테스트")
    void adminTestNotificationEvent_integration() throws Exception {
        // given
        AdminTestNotificationEvent event = AdminTestNotificationEvent.builder()
                .noticeId("1")
                .type("notice")
                .articleId("article-id")
                .postedDate("2026-03-24")
                .subject("subject")
                .category("category-name")
                .categoryKorName("카테고리")
                .baseUrl("https://admin-test-url")
                .build();

        // when
        messageAdminEventListener.sendTestNotificationEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("category-name.dev", actual.topic()),
                () -> assertEquals("[카테고리] 새로운 공지가 왔어요!", actual.title()),
                () -> assertEquals("subject", actual.body()),
                () -> assertEquals("[카테고리] 새로운 공지가 왔어요!", actual.data().get("title")),
                () -> assertEquals("subject", actual.data().get("body")),
                () -> assertEquals("notice", actual.data().get("type")),
                () -> assertEquals("1", actual.data().get("id")),
                () -> assertEquals("article-id", actual.data().get("articleId")),
                () -> assertEquals("2026-03-24", actual.data().get("postedDate")),
                () -> assertEquals("subject", actual.data().get("subject")),
                () -> assertEquals("category-name", actual.data().get("category")),
                () -> assertEquals("카테고리", actual.data().get("categoryKorName")),
                () -> assertEquals("https://admin-test-url", actual.data().get("baseUrl"))
        );
    }

    @Test
    @DisplayName("AlertSendEvent 통합 테스트")
    void alertSendEvent_integration() throws Exception {
        // given
        AlertSendEvent event = new AlertSendEvent(
                "alert-title",
                "alert-content"
        );

        // when
        messageAdminEventListener.sendAlertEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("alert-title", actual.title()),
                () -> assertEquals("alert-content", actual.body()),
                () -> assertEquals("alert-title", actual.data().get("title")),
                () -> assertEquals("alert-content", actual.data().get("body")),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("", actual.data().get("url"))
        );
    }

    @Test
    @DisplayName("AcademicTestNotificationEvent 통합 테스트")
    void academicTestNotificationEvent_integration() throws Exception {
        // given
        AcademicTestNotificationEvent event = new AcademicTestNotificationEvent(
                "academic-title",
                "academic-body"
        );

        // when
        messageAdminEventListener.sendAcademicTestNotificationEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("academicEvent.dev", actual.topic()),
                () -> assertEquals("academic-title", actual.title()),
                () -> assertEquals("academic-body", actual.body()),
                () -> assertEquals("academic-title", actual.data().get("title")),
                () -> assertEquals("academic-body", actual.data().get("body")),
                () -> assertEquals("academic", actual.data().get("type"))
        );
    }

    private Message captureSingleMessage() throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(captor.capture());
        return captor.getValue();
    }

    private record CapturedMessage(
            String topic,
            String title,
            String body,
            Map<String, String> data
    ) {
    }

    private CapturedMessage extractMessage(Message message) {
        Object notification = ReflectionTestUtils.getField(message, "notification");

        String topic = (String) ReflectionTestUtils.getField(message, "topic");
        String title = (String) ReflectionTestUtils.getField(notification, "title");
        String body = (String) ReflectionTestUtils.getField(notification, "body");

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) ReflectionTestUtils.getField(message, "data");

        return new CapturedMessage(topic, title, body, data);
    }
}
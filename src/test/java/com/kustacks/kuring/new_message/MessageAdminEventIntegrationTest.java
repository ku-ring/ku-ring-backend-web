package com.kustacks.kuring.new_message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.adapter.in.event.NewMessageAdminEventListener;
import com.kustacks.kuring.new_message.adapter.out.firebase.FirebasePushMessageAdapter;
import com.kustacks.kuring.new_message.application.assembler.AcademicTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AlertSendAssembler;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.application.service.MessageEventHandler;
import com.kustacks.kuring.new_message.application.service.NotificationSendService;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = MessageAdminEventIntegrationTest.TestConfig.class)
@DisplayName("MessageAdminEvent 전체 통합 테스트")
class MessageAdminEventIntegrationTest {

    @Resource
    private NewMessageAdminEventListener newMessageAdminEventListener;

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
        PushMessagePort pushMessagePort(FirebaseMessaging firebaseMessaging, ServerProperties serverProperties) {
            return new FirebasePushMessageAdapter(firebaseMessaging, serverProperties);
        }

        @Bean
        SendNotificationUseCase sendNotificationUseCase(PushMessagePort pushMessagePort) {
            return new NotificationSendService(pushMessagePort);
        }

        @Bean
        HandleMessageEventUseCase handleMessageEventUseCase(
                List<NotificationCommandAssembler<? extends MessageEvent>> assemblers,
                SendNotificationUseCase sendNotificationUseCase
        ) {
            return new MessageEventHandler(assemblers, sendNotificationUseCase);
        }

        @Bean
        AdminNotificationAssembler adminNotificationAssembler(ObjectMapper objectMapper) {
            return new AdminNotificationAssembler(objectMapper);
        }

        @Bean
        AdminTestNotificationAssembler adminTestNotificationAssembler(ObjectMapper objectMapper) {
            return new AdminTestNotificationAssembler(objectMapper);
        }

        @Bean
        AlertSendAssembler alertSendAssembler() {
            return new AlertSendAssembler();
        }

        @Bean
        AcademicTestNotificationAssembler academicTestNotificationAssembler() {
            return new AcademicTestNotificationAssembler();
        }

        @Bean
        NewMessageAdminEventListener messageAdminEventListener(HandleMessageEventUseCase handleMessageEventUseCase) {
            return new NewMessageAdminEventListener(handleMessageEventUseCase);
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
        newMessageAdminEventListener.sendAdminNotification(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("admin-title", actual.title()),
                () -> assertEquals("admin-body", actual.body()),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("admin-title", actual.data().get("title")),
                () -> assertEquals("admin-body", actual.data().get("body")),
                () -> assertEquals("https://admin-url", actual.data().get("url")),
                () -> assertEquals("admin", actual.data().get("messageType"))
        );
    }

    @Test
    @DisplayName("AdminTestNotificationEvent 통합 테스트")
    void adminTestNotificationEvent_integration() throws Exception {
        // given
        AdminTestNotificationEvent event = new AdminTestNotificationEvent(
                "article-id",
                "2026-03-24",
                "category-name",
                "subject",
                "카테고리",
                "https://admin-test-url"
        );

        // when
        newMessageAdminEventListener.sendAdminTestNotification(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("category-name.dev", actual.topic()),
                () -> assertEquals("[카테고리] 새로운 공지가 왔어요!", actual.title()),
                () -> assertEquals("subject", actual.body()),
                () -> assertEquals("article-id", actual.data().get("articleId")),
                () -> assertEquals("2026-03-24", actual.data().get("postedDate")),
                () -> assertEquals("subject", actual.data().get("subject")),
                () -> assertEquals("category-name", actual.data().get("category")),
                () -> assertEquals("카테고리", actual.data().get("categoryKorName")),
                () -> assertEquals("https://admin-test-url", actual.data().get("baseUrl")),
                () -> assertEquals("notice", actual.data().get("messageType"))
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
        newMessageAdminEventListener.sendAlert(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("alert-title", actual.title()),
                () -> assertEquals("alert-content", actual.body()),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("alert-title", actual.data().get("title")),
                () -> assertEquals("alert-content", actual.data().get("body")),
                () -> assertEquals("", actual.data().get("url")),
                () -> assertEquals("admin", actual.data().get("messageType"))
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
        newMessageAdminEventListener.sendAcademicTestNotification(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("academicEvent.dev", actual.topic()),
                () -> assertEquals("academic-title", actual.title()),
                () -> assertEquals("academic-body", actual.body()),
                () -> assertEquals("academic-title", actual.data().get("title")),
                () -> assertEquals("academic-body", actual.data().get("body")),
                () -> assertEquals("academic", actual.data().get("messageType"))
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

        Assertions.assertNotNull(notification);
        String topic = (String) ReflectionTestUtils.getField(message, "topic");
        String title = (String) ReflectionTestUtils.getField(notification, "title");
        String body = (String) ReflectionTestUtils.getField(notification, "body");

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) ReflectionTestUtils.getField(message, "data");

        return new CapturedMessage(topic, title, body, data);
    }
}
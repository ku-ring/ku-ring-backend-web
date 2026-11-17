package com.kustacks.kuring.new_message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.adapter.in.event.NewMessageEventListener;
import com.kustacks.kuring.new_message.adapter.out.firebase.FirebasePushMessageAdapter;
import com.kustacks.kuring.new_message.application.assembler.AcademicScheduleNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AcademicTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AlertSendAssembler;
import com.kustacks.kuring.new_message.application.assembler.ClubDeadlineNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NoticeBatchNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.application.service.MessageEventHandler;
import com.kustacks.kuring.new_message.application.service.NotificationSendService;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = MessageEventIntegrationTest.TestConfig.class)
@DisplayName("MessageEvent 전체 통합 테스트")
class MessageEventIntegrationTest {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        reset(firebaseMessaging);
    }

    @Configuration
    @EnableAsync
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
        AcademicScheduleNotificationAssembler academicScheduleNotificationAssembler() {
            return new AcademicScheduleNotificationAssembler();
        }

        @Bean
        ClubDeadlineNotificationAssembler clubDeadlineNotificationAssembler() {
            return new ClubDeadlineNotificationAssembler();
        }

        @Bean
        NoticeBatchNotificationAssembler noticeBatchNotificationAssembler(ObjectMapper objectMapper) {
            return new NoticeBatchNotificationAssembler(objectMapper);
        }

        @Bean
        NewMessageEventListener messageEventListener(HandleMessageEventUseCase handleMessageEventUseCase) {
            return new NewMessageEventListener(handleMessageEventUseCase);
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
        applicationEventPublisher.publishEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("admin-title", actual.title()),
                () -> assertEquals("admin-body", actual.body()),
                () -> assertEquals("admin-title", actual.data().get("title")),
                () -> assertEquals("admin-body", actual.data().get("body")),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("https://admin-url", actual.data().get("url")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("AdminTestNotificationEvent 통합 테스트")
    void adminTestNotificationEvent_integration() throws Exception {
        // given
        AdminTestNotificationEvent event = new AdminTestNotificationEvent(
                "1",
                "article-id",
                "2026-03-24",
                "category-name",
                "subject",
                "카테고리",
                "https://admin-test-url"
        );

        // when
        applicationEventPublisher.publishEvent(event);

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
                () -> assertEquals("https://admin-test-url", actual.data().get("baseUrl")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
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
        applicationEventPublisher.publishEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("allDevice.dev", actual.topic()),
                () -> assertEquals("alert-title", actual.title()),
                () -> assertEquals("alert-content", actual.body()),
                () -> assertEquals("alert-title", actual.data().get("title")),
                () -> assertEquals("alert-content", actual.data().get("body")),
                () -> assertEquals("admin", actual.data().get("type")),
                () -> assertEquals("", actual.data().get("url")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
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
        applicationEventPublisher.publishEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("academicEvent.dev", actual.topic()),
                () -> assertEquals("academic-title", actual.title()),
                () -> assertEquals("academic-body", actual.body()),
                () -> assertEquals("academic-title", actual.data().get("title")),
                () -> assertEquals("academic-body", actual.data().get("body")),
                () -> assertEquals("academic", actual.data().get("type")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("AcademicScheduleNotificationEvent 통합 테스트")
    void academicScheduleNotificationEvent_integration() throws Exception {
        // given
        AcademicScheduleNotificationEvent event = new AcademicScheduleNotificationEvent("학사 일정 내용");

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        Assertions.assertNotNull(actual);

        assertAll(
                () -> assertEquals("academicEvent.dev", actual.topic()),
                () -> assertEquals("[학사 일정 내용]", actual.title()),
                () -> assertEquals("오늘은 학사 일정 내용 일정이 있어요", actual.body()),
                () -> assertEquals("[학사 일정 내용]", actual.data().get("title")),
                () -> assertEquals("오늘은 학사 일정 내용 일정이 있어요", actual.data().get("body")),
                () -> assertEquals("academic", actual.data().get("type")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("ClubDeadlineNotificationEvent 통합 테스트")
    void clubDeadlineNotificationEvent_integration() throws Exception {
        // given
        ClubDeadlineNotificationEvent event = new ClubDeadlineNotificationEvent(1L, "club-name");

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("club.1.dev", actual.topic()),
                () -> assertEquals("[D-1] club-name 동아리 모집", actual.title()),
                () -> assertEquals("내일 마감되기 전에 지원하세요!", actual.body()),
                () -> assertEquals("1", actual.data().get("clubId")),
                () -> assertEquals("club", actual.data().get("type")),
                () -> assertEquals(1, actual.aps().get("mutable-content"))
        );
    }

    @Test
    @DisplayName("NoticeBatchNotificationEvent 통합 테스트")
    void noticeBatchNotificationEvent_integration() throws Exception {
        // given
        NoticeBatchNotificationEvent event = new NoticeBatchNotificationEvent(
                List.of(
                        new NoticeBatchNotificationEvent.NoticeMessageDto(
                                "article-id-1",
                                "2026-03-24",
                                "subject-1",
                                "category-1",
                                "카테고리1",
                                "https://notice-url-1"
                        ),
                        new NoticeBatchNotificationEvent.NoticeMessageDto(
                                "article-id-2",
                                "2026-03-25",
                                "subject-2",
                                "category-2",
                                "카테고리2",
                                "https://notice-url-2"
                        )
                )
        );

        // when
        applicationEventPublisher.publishEvent(event);

        // then
        List<Message> messages = captureMessages(2);

        Message first = messages.get(0);
        Message second = messages.get(1);

        CapturedMessage firstActual = extractMessage(first);
        CapturedMessage secondActual = extractMessage(second);

        assertAll(
                () -> assertEquals("category-1.dev", firstActual.topic()),
                () -> assertEquals("[카테고리1] 새로운 공지가 왔어요!", firstActual.title()),
                () -> assertEquals("subject-1", firstActual.body()),
                () -> assertEquals("article-id-1", firstActual.data().get("articleId")),
                () -> assertEquals("2026-03-24", firstActual.data().get("postedDate")),
                () -> assertEquals("subject-1", firstActual.data().get("subject")),
                () -> assertEquals("category-1", firstActual.data().get("category")),
                () -> assertEquals("카테고리1", firstActual.data().get("categoryKorName")),
                () -> assertEquals("https://notice-url-1", firstActual.data().get("baseUrl")),
                () -> assertEquals("notice", firstActual.data().get("type")),
                () -> assertEquals(1, firstActual.aps().get("mutable-content")),

                () -> assertEquals("category-2.dev", secondActual.topic()),
                () -> assertEquals("[카테고리2] 새로운 공지가 왔어요!", secondActual.title()),
                () -> assertEquals("subject-2", secondActual.body()),
                () -> assertEquals("article-id-2", secondActual.data().get("articleId")),
                () -> assertEquals("2026-03-25", secondActual.data().get("postedDate")),
                () -> assertEquals("subject-2", secondActual.data().get("subject")),
                () -> assertEquals("category-2", secondActual.data().get("category")),
                () -> assertEquals("카테고리2", secondActual.data().get("categoryKorName")),
                () -> assertEquals("https://notice-url-2", secondActual.data().get("baseUrl")),
                () -> assertEquals("notice", secondActual.data().get("type")),
                () -> assertEquals(1, secondActual.aps().get("mutable-content"))
        );
    }

    private Message captureSingleMessage() throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, timeout(1000)).send(captor.capture());
        return captor.getValue();
    }

    private List<Message> captureMessages(int count) throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, timeout(1000).times(count)).send(captor.capture());
        return captor.getAllValues();
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
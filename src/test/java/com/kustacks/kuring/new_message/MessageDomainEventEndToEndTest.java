package com.kustacks.kuring.new_message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.adapter.in.event.NewMessageDomainEventListener;
import com.kustacks.kuring.new_message.adapter.out.firebase.FirebasePushMessageAdapter;
import com.kustacks.kuring.new_message.application.assembler.AcademicScheduleNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.ClubDeadlineNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NoticeBatchNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.HandleMessageEventUseCase;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.application.service.MessageEventHandler;
import com.kustacks.kuring.new_message.application.service.NotificationSendService;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
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

@SpringJUnitConfig(classes = MessageDomainEventEndToEndTest.TestConfig.class)
@DisplayName("MessageDomainEvent 전체 통합 테스트")
class MessageDomainEventEndToEndTest {

    @Resource
    private NewMessageDomainEventListener newMessageDomainEventListener;

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
        NewMessageDomainEventListener messageDomainEventListener(HandleMessageEventUseCase handleMessageEventUseCase) {
            return new NewMessageDomainEventListener(handleMessageEventUseCase);
        }
    }

    @Test
    @DisplayName("AcademicScheduleNotificationEvent end-to-end")
    void academicScheduleNotificationEvent_endToEnd() throws Exception {
        // given
        AcademicScheduleNotificationEvent event = new AcademicScheduleNotificationEvent("학사 일정 내용");

        // when
        newMessageDomainEventListener.sendAcademicScheduleNotification(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        Assertions.assertNotNull(actual);

        assertAll(
                () -> assertEquals("academicEvent.dev", actual.topic()),
                () -> assertEquals("[학사 일정 내용]", actual.title()),
                () -> assertEquals("오늘은 학사 일정 내용 일정이 있어요", actual.body()),
                () -> assertEquals("[학사 일정 내용]", actual.data().get("title")),
                () -> assertEquals("오늘은 학사 일정 내용 일정이 있어요", actual.data().get("body")),
                () -> assertEquals("academic", actual.data().get("messageType"))
        );
    }

    @Test
    @DisplayName("ClubDeadlineNotificationEvent end-to-end")
    void clubDeadlineNotificationEvent_endToEnd() throws Exception {
        // given
        ClubDeadlineNotificationEvent event = new ClubDeadlineNotificationEvent(1L, "club-name");

        // when
        newMessageDomainEventListener.sendClubDeadlineNotification(event);

        // then
        CapturedMessage actual = extractMessage(captureSingleMessage());

        assertAll(
                () -> assertEquals("club.1.dev", actual.topic()),
                () -> assertEquals("[D-1] club-name 동아리 모집", actual.title()),
                () -> assertEquals("내일 마감되기 전에 지원하세요!", actual.body()),
                () -> assertEquals("1", actual.data().get("clubId")),
                () -> assertEquals("club", actual.data().get("messageType"))
        );
    }

    @Test
    @DisplayName("NoticeBatchNotificationEvent end-to-end")
    void noticeBatchNotificationEvent_endToEnd() throws Exception {
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
        newMessageDomainEventListener.sendNoticeBatchNotification(event);

        // then
        List<Message> messages = captureMessages(2);

        Message first = messages.get(0);
        Message second = messages.get(1);

        assertMessage(
                first,
                "category-1.dev",
                "[카테고리1] 새로운 공지가 왔어요!",
                "subject-1",
                Map.of(
                        "articleId", "article-id-1",
                        "postedDate", "2026-03-24",
                        "subject", "subject-1",
                        "category", "category-1",
                        "categoryKorName", "카테고리1",
                        "baseUrl", "https://notice-url-1",
                        "messageType", "notice"
                )
        );

        assertMessage(
                second,
                "category-2.dev",
                "[카테고리2] 새로운 공지가 왔어요!",
                "subject-2",
                Map.of(
                        "articleId", "article-id-2",
                        "postedDate", "2026-03-25",
                        "subject", "subject-2",
                        "category", "category-2",
                        "categoryKorName", "카테고리2",
                        "baseUrl", "https://notice-url-2",
                        "messageType", "notice"
                )
        );
    }

    private Message captureSingleMessage() throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging).send(captor.capture());
        return captor.getValue();
    }

    private List<Message> captureMessages(int count) throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, times(count)).send(captor.capture());
        return captor.getAllValues();
    }

    private record CapturedMessage(
            String topic,
            String title,
            String body,
            Map<String, String> data
    ) {
    }

    private void assertMessage(
            Message actual,
            String expectedTopic,
            String expectedTitle,
            String expectedBody,
            Map<String, String> expectedData
    ) {
        CapturedMessage capturedMessage = extractMessage(actual);

        assertAll(
                () -> assertEquals(expectedTopic, capturedMessage.topic()),
                () -> assertEquals(expectedTitle, capturedMessage.title()),
                () -> assertEquals(expectedBody, capturedMessage.body()),
                () -> expectedData.forEach((key, value) -> assertEquals(value, capturedMessage.data().get(key)))
        );
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
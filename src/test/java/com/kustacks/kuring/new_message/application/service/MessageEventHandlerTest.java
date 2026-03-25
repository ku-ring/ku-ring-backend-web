package com.kustacks.kuring.new_message.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.new_message.application.assembler.AcademicScheduleNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AcademicTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AdminTestNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.AlertSendAssembler;
import com.kustacks.kuring.new_message.application.assembler.ClubDeadlineNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NoticeBatchNotificationAssembler;
import com.kustacks.kuring.new_message.application.assembler.NotificationCommandAssembler;
import com.kustacks.kuring.new_message.application.port.in.SendNotificationUseCase;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicNames;
import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import com.kustacks.kuring.new_message.domain.event.AcademicScheduleNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AcademicTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AdminTestNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.AlertSendEvent;
import com.kustacks.kuring.new_message.domain.event.ClubDeadlineNotificationEvent;
import com.kustacks.kuring.new_message.domain.event.MessageEvent;
import com.kustacks.kuring.new_message.domain.event.NoticeBatchNotificationEvent;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("이벤트 헨들러를 통해 각 이벤트에 맞는 assembler가 실행된다.")
class MessageEventHandlerTest {

    @Mock
    private SendNotificationUseCase sendNotificationUseCase;

    private MessageEventHandler messageEventHandler;

    @BeforeEach
    void setUp() {
        List<NotificationCommandAssembler<? extends MessageEvent>> assemblers = List.of(
                new AcademicScheduleNotificationAssembler(),
                new AcademicTestNotificationAssembler(),
                new AdminNotificationAssembler(new ObjectMapper()),
                new AdminTestNotificationAssembler(new ObjectMapper()),
                new AlertSendAssembler(),
                new ClubDeadlineNotificationAssembler(),
                new NoticeBatchNotificationAssembler(new ObjectMapper())
        );

        messageEventHandler = new MessageEventHandler(
                assemblers,
                sendNotificationUseCase
        );
    }

    @Test
    @DisplayName("AdminNotificationEvent가 들어오면 admin 알림 명령을 생성한다")
    void handle_adminNotificationEvent() {
        // given
        AdminNotificationEvent event =
                new AdminNotificationEvent("admin-title", "admin-body", "https://admin-url");
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.ADMIN, command.messageType()),
                () -> assertEquals("admin-title", command.content().title()),
                () -> assertEquals("admin-body", command.content().body()),
                () -> assertEquals("https://admin-url", command.data().get("url")),
                () -> assertEquals(TopicNames.ALL_DEVICE_SUBSCRIBED_TOPIC, command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("AdminTestNotificationEvent가 들어오면 공지 테스트용 명령을 생성한다")
    void handle_adminTestNotificationEvent() {
        // given
        AdminTestNotificationEvent event = new AdminTestNotificationEvent(
                "article-id",
                "2026-03-16",
                "category-name",
                "subject",
                "카테고리",
                "https://admin-test-url"
        );
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.NOTICE, command.messageType()),
                () -> assertEquals("article-id", command.data().get("articleId")),
                () -> assertEquals("2026-03-16", command.data().get("postedDate")),
                () -> assertEquals("category-name", command.data().get("category")),
                () -> assertEquals("subject", command.data().get("subject")),
                () -> assertEquals("카테고리", command.data().get("categoryKorName")),
                () -> assertEquals("https://admin-test-url", command.data().get("baseUrl")),
                () -> assertEquals("[카테고리] 새로운 공지가 왔어요!", command.content().title()),
                () -> assertEquals("subject", command.content().body()),
                () -> assertEquals("category-name", command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("AcademicTestNotificationEvent가 들어오면 학사 테스트용 명령을 생성한다")
    void handle_academicTestNotificationEvent() {
        // given
        AcademicTestNotificationEvent event =
                new AcademicTestNotificationEvent("academic-test-title", "academic-test-body");
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.ACADEMIC, command.messageType()),
                () -> assertEquals("academic-test-title", command.content().title()),
                () -> assertEquals("academic-test-body", command.content().body()),
                () -> assertTrue(command.data().isEmpty()),
                () -> assertEquals(TopicNames.ACADEMIC_EVENT_TOPIC, command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("AcademicScheduleNotificationEvent가 들어오면 학사 일정 명령을 생성한다")
    void handle_academicScheduleNotificationEvent() {
        // given
        AcademicScheduleNotificationEvent event =
                new AcademicScheduleNotificationEvent("학사 일정 내용");
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.ACADEMIC, command.messageType()),
                () -> assertEquals("[학사 일정 내용]", command.content().title()),
                () -> assertEquals("오늘은 학사 일정 내용 일정이 있어요", command.content().body()),
                () -> assertTrue(command.data().isEmpty()),
                () -> assertEquals(TopicNames.ACADEMIC_EVENT_TOPIC, command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("AlertSendEvent가 들어오면 alert 명령을 생성한다")
    void handle_alertSendEvent() {
        // given
        AlertSendEvent event = new AlertSendEvent("alert-title", "alert-content");
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.ADMIN, command.messageType()),
                () -> assertEquals("alert-title", command.content().title()),
                () -> assertEquals("alert-content", command.content().body()),
                () -> assertEquals(2, command.data().size()),
                () -> assertEquals(TopicNames.ALL_DEVICE_SUBSCRIBED_TOPIC, command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("ClubDeadlineNotificationEvent가 들어오면 동아리 마감 알림 명령을 생성한다")
    void handle_clubDeadlineNotificationEvent() {
        // given
        ClubDeadlineNotificationEvent event = new ClubDeadlineNotificationEvent(
                1L,
                "club-name"
        );
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        NotificationCommand command = captureSingleCommand();

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(MessageType.CLUB, command.messageType()),
                () -> assertEquals("[D-1] club-name 동아리 모집", command.content().title()),
                () -> assertEquals("내일 마감되기 전에 지원하세요!", command.content().body()),
                () -> assertEquals("1", String.valueOf(command.data().get("clubId"))),
                () -> assertEquals(TopicNames.clubTopic(1L), command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("NoticeBatchNotificationEvent가 들어오면 공지 배치 알림 명령들을 생성한다")
    void handle_noticeBatchNotificationEvent() {
        // given
        NoticeBatchNotificationEvent event = new NoticeBatchNotificationEvent(
                List.of(
                        new NoticeBatchNotificationEvent.NoticeMessageDto(
                                "article-id",
                                "2026-03-16",
                                "subject",
                                "category",
                                "카테고리",
                                "https://notice-url"
                        )
                )
        );
        when(sendNotificationUseCase.sendAll(anyList())).thenReturn(1);

        // when
        int result = messageEventHandler.handle(event);

        // then
        List<NotificationCommand> commands = captureCommands();
        NotificationCommand command = commands.get(0);

        assertAll(
                () -> assertEquals(1, result),
                () -> assertEquals(1, commands.size()),
                () -> assertEquals(MessageType.NOTICE, command.messageType()),
                () -> assertEquals("[카테고리] 새로운 공지가 왔어요!", command.content().title()),
                () -> assertEquals("subject", command.content().body()),
                () -> assertEquals("article-id", command.data().get("articleId")),
                () -> assertEquals("2026-03-16", command.data().get("postedDate")),
                () -> assertEquals("subject", command.data().get("subject")),
                () -> assertEquals("category", command.data().get("category")),
                () -> assertEquals("카테고리", command.data().get("categoryKorName")),
        () -> assertEquals(TopicNames.noticeTopic("category"), command.target().topic()),
                () -> assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, command.target().topicSuffixPolicy())
        );
    }

    @Test
    @DisplayName("지원하지 않는 이벤트면 IllegalArgumentException이 발생한다")
    void handle_unsupportedEvent() {
        // given
        MessageEvent unsupportedEvent = new MessageEvent() {
        };

        // when
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> messageEventHandler.handle(unsupportedEvent));

        // then
        assertTrue(exception.getMessage().contains("지원하지 않는 메시지 이벤트 타입"));
    }

    @Test
    @DisplayName("하나의 이벤트에 assembler가 중복 매칭되면 IllegalStateException이 발생한다")
    void handle_duplicateMatchedAssembler() {
        // given
        NotificationCommandAssembler<MessageEvent> assembler1 = new DuplicateAssembler();
        NotificationCommandAssembler<MessageEvent> assembler2 = new DuplicateAssembler();

        messageEventHandler = new MessageEventHandler(
                List.of(assembler1, assembler2),
                sendNotificationUseCase
        );

        MessageEvent event = new MessageEvent() {
        };

        // when
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> messageEventHandler.handle(event)
        );

        // then
        assertTrue(exception.getMessage().contains("중복 매칭된 메시지 이벤트 타입"));
    }

    private NotificationCommand captureSingleCommand() {
        List<NotificationCommand> commands = captureCommands();
        return commands.get(0);
    }

    @SuppressWarnings("unchecked")
    private List<NotificationCommand> captureCommands() {
        ArgumentCaptor<List<NotificationCommand>> captor = ArgumentCaptor.forClass(List.class);
        verify(sendNotificationUseCase).sendAll(captor.capture());
        return captor.getValue();
    }

    private static class DuplicateAssembler implements NotificationCommandAssembler<MessageEvent> {

        @Override
        public boolean supports(MessageEvent event) {
            return true;
        }

        @Override
        public List<NotificationCommand> assemble(MessageEvent event) {
            return List.of(
                    new NotificationCommand(
                            NotificationTarget.topic("topic"),
                            NotificationContent.of("title", "body"),
                            MessageType.ADMIN,
                            java.util.Map.of()
                    )
            );
        }
    }
}
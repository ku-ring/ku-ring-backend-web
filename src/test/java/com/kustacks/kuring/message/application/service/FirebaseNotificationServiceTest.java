package com.kustacks.kuring.message.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.dto.AcademicTestNotificationCommand;
import com.kustacks.kuring.message.application.port.in.dto.AdminNotificationCommand;
import com.kustacks.kuring.message.application.port.in.dto.AdminTestNotificationCommand;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.notice.domain.Notice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FirebaseNotificationService")
class FirebaseNotificationServiceTest {

    @InjectMocks
    private FirebaseNotificationService service;

    @Mock
    private FirebaseMessagingPort firebaseMessagingPort;

    @Mock
    private ServerProperties serverProperties;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("관리자 알림은 type과 mutable-content를 포함한다")
    @Test
    void should_include_type_and_mutable_content_for_admin_notification() throws Exception {
        when(serverProperties.ifDevThenAddSuffix("allDevice")).thenReturn("allDevice.dev");

        service.sendNotificationByAdmin(new AdminNotificationCommand("점검", "오늘 점검이 있습니다.", "https://kuring.com"));

        Message sent = captureSentMessage();
        Map<String, String> data = getData(sent);

        assertAll(
                () -> assertThat(getTopic(sent)).isEqualTo("allDevice.dev"),
                () -> assertThat(data).containsEntry("type", "admin"),
                () -> assertThat(data).containsEntry("title", "점검"),
                () -> assertThat(data).containsEntry("body", "오늘 점검이 있습니다."),
                () -> assertThat(data).containsEntry("url", "https://kuring.com"),
                () -> assertThat(getAps(sent)).containsEntry("mutable-content", 1)
        );
    }

    @DisplayName("테스트 공지 알림은 type과 mutable-content를 포함한다")
    @Test
    void should_include_type_and_mutable_content_for_test_notice_notification() throws Exception {
        when(serverProperties.addDevSuffix("student")).thenReturn("student.dev");

        service.sendTestNotificationByAdmin(new AdminTestNotificationCommand(
                "1234",
                "1234",
                "2026-03-17 12:00:00",
                "student",
                "테스트 공지입니다.",
                "학생",
                "https://kuring.com/notices/1234"
        ));

        Message sent = captureSentMessage();
        Map<String, String> data = getData(sent);

        assertAll(
                () -> assertThat(getTopic(sent)).isEqualTo("student.dev"),
                () -> assertThat(data).containsEntry("type", "notice"),
                () -> assertThat(data).containsEntry("articleId", "1234"),
                () -> assertThat(data).containsEntry("category", "student"),
                () -> assertThat(data).containsEntry("subject", "테스트 공지입니다."),
                () -> assertThat(getAps(sent)).containsEntry("mutable-content", 1)
        );
    }

    @DisplayName("학사일정 테스트 알림은 type과 mutable-content를 포함한다")
    @Test
    void should_include_type_and_mutable_content_for_academic_test_notification() throws Exception {
        when(serverProperties.addDevSuffix("academicEvent")).thenReturn("academicEvent.dev");

        service.sendAcademicTestNotification(new AcademicTestNotificationCommand("개강", "오늘은 개강 일정이 있어요"));

        Message sent = captureSentMessage();
        Map<String, String> data = getData(sent);

        assertAll(
                () -> assertThat(getTopic(sent)).isEqualTo("academicEvent.dev"),
                () -> assertThat(data).containsEntry("type", "academic"),
                () -> assertThat(data).containsEntry("title", "개강"),
                () -> assertThat(data).containsEntry("body", "오늘은 개강 일정이 있어요"),
                () -> assertThat(getAps(sent)).containsEntry("mutable-content", 1)
        );
    }

    @DisplayName("자동 공지 알림은 type과 mutable-content를 포함한다")
    @Test
    void should_include_type_and_mutable_content_for_notice_notification() throws Exception {
        Notice notice = mock(Notice.class);
        when(notice.getId()).thenReturn(1L);
        when(notice.getArticleId()).thenReturn("1234");
        when(notice.getPostedDate()).thenReturn("2026-03-17 12:00:00");
        when(notice.getSubject()).thenReturn("자동 공지입니다.");
        when(notice.getCategoryName()).thenReturn("student");
        when(notice.getCategoryKoreaName()).thenReturn("학생");
        when(notice.getUrl()).thenReturn("https://kuring.com/notices/1234");
        when(serverProperties.ifDevThenAddSuffix("student")).thenReturn("student.dev");

        service.sendNotifications(List.of(notice));

        Message sent = captureSentMessage();
        Map<String, String> data = getData(sent);

        assertAll(
                () -> assertThat(getTopic(sent)).isEqualTo("student.dev"),
                () -> assertThat(data).containsEntry("type", "notice"),
                () -> assertThat(data).containsEntry("id", "1"),
                () -> assertThat(data).containsEntry("articleId", "1234"),
                () -> assertThat(data).containsEntry("category", "student"),
                () -> assertThat(getAps(sent)).containsEntry("mutable-content", 1)
        );
    }

    private Message captureSentMessage() throws Exception {
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessagingPort).send(captor.capture());
        return captor.getValue();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getData(Message sent) {
        return (Map<String, String>) ReflectionTestUtils.getField(sent, "data");
    }

    private String getTopic(Message sent) {
        return (String) ReflectionTestUtils.getField(sent, "topic");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAps(Message sent) {
        Map<String, Object> apnsPayload = (Map<String, Object>) ReflectionTestUtils.getField(
                ReflectionTestUtils.getField(sent, "apnsConfig"),
                "payload"
        );
        return (Map<String, Object>) apnsPayload.get("aps");
    }
}

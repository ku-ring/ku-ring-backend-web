package com.kustacks.kuring.new_message.application.service.intergrationTest;

import com.kustacks.kuring.new_message.adapter.out.firebase.NewFakeFirebaseAdapter;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.application.service.NotificationSendService;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = NotificationSendServiceIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("알림 전송 서비스 통합 테스트")
class NotificationSendServiceIntegrationTest {

    @Autowired
    private NotificationSendService notificationSendService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        NotificationSendService notificationSendService(PushMessagePort pushMessagePort) {
            return new NotificationSendService(pushMessagePort);
        }

        @Bean
        PushMessagePort pushMessagePort() {
            return new NewFakeFirebaseAdapter();
        }
    }

    @Test
    @DisplayName("FakeFirebaseAdapter와 연동하여 단건 알림 전송에 성공한다")
    void send_success() {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic"),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                Map.of()
        );

        // when
        boolean result = notificationSendService.send(command);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("FakeFirebaseAdapter와 연동하여 여러 알림 전송 시 성공 개수를 반환한다")
    void sendAll_success() {
        // given
        List<NotificationCommand> commands = List.of(
                new NotificationCommand(NotificationTarget.topic("topic1"), NotificationContent.of("title1", "body1"), MessageType.ADMIN, Map.of()),
                new NotificationCommand(NotificationTarget.topic("topic2"), NotificationContent.of("title2", "body2"), MessageType.NOTICE, Map.of()),
                new NotificationCommand(NotificationTarget.topic("topic3"), NotificationContent.of("title3", "body3"), MessageType.ACADEMIC, Map.of())
        );

        // when
        int result = notificationSendService.sendAll(commands);

        // then
        assertEquals(3, result);
    }
}

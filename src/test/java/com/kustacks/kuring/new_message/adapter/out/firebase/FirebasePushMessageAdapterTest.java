package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.domain.enums.MessageType;
import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.domain.model.NotificationContent;
import com.kustacks.kuring.new_message.domain.model.NotificationTarget;
import com.kustacks.kuring.new_message.exception.message.MessageSendException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebasePushMessageAdapterTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private ServerProperties serverProperties;

    @InjectMocks
    private FirebasePushMessageAdapter firebasePushMessageAdapter;

    @Test
    @DisplayName("IF_DEV_THEN_ADD_SUFFIX 정책이면 ifDevThenAddSuffix를 사용해 Firebase로 전송한다")
    void send_ifDevThenAddSuffix() throws Exception {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic", TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX),
                NotificationContent.of("title", "body"),
                MessageType.NOTICE,
                Map.of("key", "value")
        );

        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic.dev");

        // when
        assertDoesNotThrow(() -> firebasePushMessageAdapter.send(command));

        // then
        assertAll(
                () -> verify(serverProperties).ifDevThenAddSuffix("topic"),
                () -> verify(firebaseMessaging).send(any(Message.class))
        );
    }

    @Test
    @DisplayName("ALWAYS_ADD_DEV_SUFFIX 정책이면 addDevSuffix를 사용해 Firebase로 전송한다")
    void send_alwaysAddDevSuffix() throws Exception {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic", TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX),
                NotificationContent.of("title", "body"),
                MessageType.ACADEMIC,
                Map.of()
        );

        when(serverProperties.addDevSuffix("topic")).thenReturn("topic.dev");

        // when
        assertDoesNotThrow(() -> firebasePushMessageAdapter.send(command));

        // then
        assertAll(
                () -> verify(serverProperties).addDevSuffix("topic"),
                () -> verify(firebaseMessaging).send(any(Message.class))
        );
    }

    @Test
    @DisplayName("FirebaseMessagingException이 발생하면 MessageSendException으로 변환한다")
    void send_fail_wrapMessageSendException() throws Exception {
        // given
        NotificationCommand command = new NotificationCommand(
                NotificationTarget.topic("topic", TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX),
                NotificationContent.of("title", "body"),
                MessageType.ADMIN,
                Map.of()
        );

        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic");
        when(firebaseMessaging.send(any(Message.class)))
                .thenThrow(mock(FirebaseMessagingException.class));

        // when & then
        assertThrows(MessageSendException.class, () -> firebasePushMessageAdapter.send(command));
    }
}
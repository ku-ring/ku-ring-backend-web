package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseSubscriptionAdapterTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private ServerProperties serverProperties;

    @InjectMocks
    private FirebaseSubscriptionAdapter firebaseSubscriptionAdapter;

    @Test
    @DisplayName("구독 시 ifDevThenAddSuffix로 변환한 토픽으로 Firebase를 호출한다")
    void subscribe_success() throws Exception {
        // given
        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic.dev");

        // when
        assertDoesNotThrow(() -> firebaseSubscriptionAdapter.subscribe("token", "topic"));

        // then
        assertAll(
                () -> verify(serverProperties).ifDevThenAddSuffix("topic"),
                () -> verify(firebaseMessaging).subscribeToTopic(List.of("token"), "topic.dev")
        );
    }

    @Test
    @DisplayName("구독 중 FirebaseMessagingException이 발생하면 MessageSubscribeException으로 변환한다")
    void subscribe_fail_wrapMessageSubscribeException() throws Exception {
        // given
        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic.dev");
        when(firebaseMessaging.subscribeToTopic(List.of("token"), "topic.dev"))
                .thenThrow(mock(FirebaseMessagingException.class));

        // when & then
        assertThrows(MessageSubscribeException.class,
                () -> firebaseSubscriptionAdapter.subscribe("token", "topic"));
    }

    @Test
    @DisplayName("구독 해제 시 ifDevThenAddSuffix로 변환한 토픽으로 Firebase를 호출한다")
    void unsubscribe_success() throws Exception {
        // given
        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic.dev");

        // when
        assertDoesNotThrow(() -> firebaseSubscriptionAdapter.unsubscribe("token", "topic"));

        // then
        assertAll(
                () -> verify(serverProperties).ifDevThenAddSuffix("topic"),
                () -> verify(firebaseMessaging).unsubscribeFromTopic(List.of("token"), "topic.dev")
        );
    }

    @Test
    @DisplayName("구독 해제 중 FirebaseMessagingException이 발생하면 MessageUnSubscribeException으로 변환한다")
    void unsubscribe_fail_wrapMessageUnSubscribeException() throws Exception {
        // given
        when(serverProperties.ifDevThenAddSuffix("topic")).thenReturn("topic.dev");
        when(firebaseMessaging.unsubscribeFromTopic(List.of("token"), "topic.dev"))
                .thenThrow(mock(FirebaseMessagingException.class));

        // when & then
        assertThrows(MessageUnSubscribeException.class,
                () -> firebaseSubscriptionAdapter.unsubscribe("token", "topic"));
    }

    @Test
    @DisplayName("dry-run 전송에 성공하면 true를 반환한다")
    void validate_success() throws Exception {
        // given
        String token = "valid-token";

        // when
        boolean result = firebaseSubscriptionAdapter.validate(token);

        // then
        assertAll(
                () -> assertTrue(result),
                () -> verify(firebaseMessaging).send(any(Message.class), eq(true))
        );
    }

    @Test
    @DisplayName("dry-run 전송 중 예외가 발생하면 false를 반환한다")
    void validate_fail() throws Exception {
        // given
        String token = "invalid-token";
        when(firebaseMessaging.send(any(Message.class), eq(true)))
                .thenThrow(new RuntimeException("invalid token"));

        // when
        boolean result = firebaseSubscriptionAdapter.validate(token);

        // then
        assertAll(
                () -> assertFalse(result),
                () -> verify(firebaseMessaging).send(any(Message.class), eq(true))
        );
    }
}

package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;
import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TopicSubscriptionServiceTest {

    @Mock
    private TopicSubscriptionPort topicSubscriptionPort;

    @InjectMocks
    private TopicSubscriptionService topicSubscriptionService;

    @Test
    @DisplayName("토픽 구독을 포트에 위임한다")
    void subscribe_success() {
        // when
        assertDoesNotThrow(() -> topicSubscriptionService.subscribe("token", "topic"));

        // then
        verify(topicSubscriptionPort).subscribe("token", "topic");
    }

    @Test
    @DisplayName("토픽 구독 중 예외가 발생하면 MessageSubscribeException으로 감싼다")
    void subscribe_fail_wrapMessageSubscribeException() {
        // given
        doThrow(new RuntimeException()).when(topicSubscriptionPort).subscribe("token", "topic");

        // when & then
        assertThrows(MessageSubscribeException.class, () -> topicSubscriptionService.subscribe("token", "topic"));
    }

    @Test
    @DisplayName("토픽 구독 해제를 포트에 위임한다")
    void unsubscribe_success() {
        // when
        assertDoesNotThrow(() -> topicSubscriptionService.unsubscribe("token", "topic"));

        // then
        verify(topicSubscriptionPort).unsubscribe("token", "topic");
    }

    @Test
    @DisplayName("토픽 구독 해제 중 예외가 발생하면 MessageUnSubscribeException으로 감싼다")
    void unsubscribe_fail_wrapMessageUnSubscribeException() {
        // given
        doThrow(new RuntimeException()).when(topicSubscriptionPort).unsubscribe("token", "topic");

        // when & then
        assertThrows(MessageUnSubscribeException.class, () -> topicSubscriptionService.unsubscribe("token", "topic"));
    }
}

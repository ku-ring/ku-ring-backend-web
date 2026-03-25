package com.kustacks.kuring.new_message.adapter.in.event;

import com.kustacks.kuring.new_message.application.port.in.ManageTopicSubscriptionUseCase;
import com.kustacks.kuring.new_message.domain.event.UserSubscribeEvent;
import com.kustacks.kuring.new_message.domain.event.UserUnsubscribeEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewMessageUserEventListenerTest {

    @Mock
    private ManageTopicSubscriptionUseCase manageTopicSubscriptionUseCase;

    @InjectMocks
    private NewMessageUserEventListener newMessageUserEventListener;

    @Test
    @DisplayName("UserSubscribeEvent를 수신하면 subscribe에 위임한다")
    void subscribeEvent_success() {
        // given
        UserSubscribeEvent event = new UserSubscribeEvent("token", "topic");

        // when
        newMessageUserEventListener.subscribeEvent(event);

        // then
        verify(manageTopicSubscriptionUseCase).subscribe("token", "topic");
    }

    @Test
    @DisplayName("UserUnsubscribeEvent를 수신하면 unsubscribe에 위임한다")
    void unsubscribeEvent_success() {
        // given
        UserUnsubscribeEvent event = new UserUnsubscribeEvent("token", "topic");

        // when
        newMessageUserEventListener.unsubscribeEvent(event);

        // then
        verify(manageTopicSubscriptionUseCase).unsubscribe("token", "topic");
    }

    @Test
    @DisplayName("recover 메서드는 예외 없이 종료된다")
    void recover_methods() {
        // given
        Exception exception = new RuntimeException("fail");

        // when & then
        assertAll(
                () -> assertDoesNotThrow(() ->
                        newMessageUserEventListener.recoverSubscribe(exception, new UserSubscribeEvent("token", "topic"))
                ),

                () -> assertDoesNotThrow(() ->
                        newMessageUserEventListener.recoverUnsubscribe(exception, new UserUnsubscribeEvent("token", "topic"))
                )
        );
    }
}

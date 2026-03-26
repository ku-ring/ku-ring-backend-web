package com.kustacks.kuring.message;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.adapter.in.event.MessageUserEventListener;
import com.kustacks.kuring.message.adapter.in.event.dto.UserSubscribeEvent;
import com.kustacks.kuring.message.adapter.in.event.dto.UserUnsubscribeEvent;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import com.kustacks.kuring.message.application.service.FirebaseSubscribeService;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = MessageUserEventIntegrationTest.TestConfig.class)
@DisplayName("MessageUserEvent 전체 통합 테스트")
class MessageUserEventIntegrationTest {

    @Resource
    private MessageUserEventListener messageUserEventListener;

    @Resource
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        reset(firebaseMessaging);
    }

    @Configuration
    static class TestConfig {

        @Bean
        FirebaseMessaging firebaseMessaging() {
            return mock(FirebaseMessaging.class);
        }

        @Bean
        ServerProperties serverProperties() {
            ServerProperties serverProperties = mock(ServerProperties.class);
            when(serverProperties.ifDevThenAddSuffix(anyString()))
                    .thenAnswer(invocation -> invocation.getArgument(0) + ".dev");
            return serverProperties;
        }

        @Bean
        FirebaseSubscribePort firebaseSubscribePort(FirebaseMessaging firebaseMessaging) {
            return new FirebaseSubscribePort() {
                @Override
                public void subscribeToTopic(List<String> tokens, String topic) throws FirebaseSubscribeException {
                    try {
                        firebaseMessaging.subscribeToTopic(tokens, topic);
                    } catch (FirebaseMessagingException e) {
                        throw new FirebaseSubscribeException();
                    }
                }

                @Override
                public void unsubscribeFromTopic(List<String> tokens, String topic) throws FirebaseUnSubscribeException {
                    try {
                        firebaseMessaging.unsubscribeFromTopic(tokens, topic);
                    } catch (FirebaseMessagingException e) {
                        throw new FirebaseUnSubscribeException();
                    }
                }
            };
        }

        @Bean
        FirebaseMessagingPort firebaseMessagingPort(FirebaseMessaging firebaseMessaging) {
            return firebaseMessaging::send;
        }

        @Bean
        FirebaseWithUserUseCase firebaseWithUserUseCase(
                FirebaseSubscribePort firebaseSubscribePort,
                FirebaseMessagingPort firebaseMessagingPort,
                ServerProperties serverProperties
        ) {
            return new FirebaseSubscribeService(firebaseSubscribePort, firebaseMessagingPort, serverProperties);
        }

        @Bean
        MessageUserEventListener messageUserEventListener(FirebaseWithUserUseCase firebaseWithUserUseCase) {
            return new MessageUserEventListener(firebaseWithUserUseCase);
        }
    }

    @Test
    @DisplayName("UserSubscribeEvent 통합 테스트")
    void userSubscribeEvent_integration() throws Exception {
        // given
        UserSubscribeEvent event = new UserSubscribeEvent("token-1", "topic-1");

        // when
        messageUserEventListener.subscribeEvent(event);

        // then
        verify(firebaseMessaging).subscribeToTopic(List.of("token-1"), "topic-1.dev");
    }

    @Test
    @DisplayName("UserUnsubscribeEvent 통합 테스트")
    void userUnsubscribeEvent_integration() throws Exception {
        // given
        UserUnsubscribeEvent event = new UserUnsubscribeEvent("token-2", "topic-2");

        // when
        messageUserEventListener.unSubscribeEvent(event);

        // then
        verify(firebaseMessaging).unsubscribeFromTopic(List.of("token-2"), "topic-2.dev");
    }
}

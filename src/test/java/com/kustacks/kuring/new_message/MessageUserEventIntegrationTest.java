package com.kustacks.kuring.new_message;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.adapter.in.event.NewMessageUserEventListener;
import com.kustacks.kuring.new_message.adapter.out.firebase.FirebaseSubscriptionAdapter;
import com.kustacks.kuring.new_message.application.port.in.ManageTopicSubscriptionUseCase;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;
import com.kustacks.kuring.new_message.application.service.TopicSubscriptionService;
import com.kustacks.kuring.new_message.domain.event.UserSubscribeEvent;
import com.kustacks.kuring.new_message.domain.event.UserUnsubscribeEvent;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = MessageUserEventIntegrationTest.TestConfig.class)
@DisplayName("MessageUserEvent 전체 통합 테스트")
class MessageUserEventIntegrationTest {

    @Resource
    private NewMessageUserEventListener newMessageUserEventListener;

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
        TopicSubscriptionPort topicSubscriptionPort(FirebaseMessaging firebaseMessaging, ServerProperties serverProperties) {
            return new FirebaseSubscriptionAdapter(firebaseMessaging, serverProperties);
        }

        @Bean
        ManageTopicSubscriptionUseCase manageTopicSubscriptionUseCase(TopicSubscriptionPort topicSubscriptionPort) {
            return new TopicSubscriptionService(topicSubscriptionPort);
        }

        @Bean
        NewMessageUserEventListener messageUserEventListener(ManageTopicSubscriptionUseCase manageTopicSubscriptionUseCase) {
            return new NewMessageUserEventListener(manageTopicSubscriptionUseCase);
        }
    }

    @Test
    @DisplayName("UserSubscribeEvent 통합 테스트")
    void userSubscribeEvent_integration() throws Exception {
        // given
        UserSubscribeEvent event = new UserSubscribeEvent("token-1", "topic-1");

        // when
        newMessageUserEventListener.subscribeEvent(event);

        // then
        verify(firebaseMessaging).subscribeToTopic(List.of("token-1"), "topic-1.dev");
    }

    @Test
    @DisplayName("UserUnsubscribeEvent 통합 테스트")
    void userUnsubscribeEvent_integration() throws Exception {
        // given
        UserUnsubscribeEvent event = new UserUnsubscribeEvent("token-2", "topic-2");

        // when
        newMessageUserEventListener.unsubscribeEvent(event);

        // then
        verify(firebaseMessaging).unsubscribeFromTopic(List.of("token-2"), "topic-2.dev");
    }
}

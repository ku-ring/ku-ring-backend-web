package com.kustacks.kuring.new_message.application.service.intergrationTest;

import com.kustacks.kuring.new_message.adapter.out.firebase.NewFakeFirebaseAdapter;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;
import com.kustacks.kuring.new_message.application.service.TopicSubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringJUnitConfig(classes = TopicSubscriptionServiceIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("토픽 구독 서비스 통합 테스트")
class TopicSubscriptionServiceIntegrationTest {

    @Autowired
    private TopicSubscriptionService topicSubscriptionService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        TopicSubscriptionService topicSubscriptionService(TopicSubscriptionPort topicSubscriptionPort) {
            return new TopicSubscriptionService(topicSubscriptionPort);
        }

        @Bean
        TopicSubscriptionPort topicSubscriptionPort() {
            return new NewFakeFirebaseAdapter();
        }
    }

    @Test
    @DisplayName("FakeFirebaseAdapter와 연동하여 토픽 구독에 성공한다")
    void subscribe_success() {
        assertDoesNotThrow(() -> topicSubscriptionService.subscribe("token", "topic"));
    }

    @Test
    @DisplayName("FakeFirebaseAdapter와 연동하여 토픽 구독 해제에 성공한다")
    void unsubscribe_success() {
        assertDoesNotThrow(() -> topicSubscriptionService.unsubscribe("token", "topic"));
    }
}

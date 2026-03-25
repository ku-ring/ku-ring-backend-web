package com.kustacks.kuring.new_message.domain.model;

import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationTargetTest {

    @Test
    @DisplayName("topicSuffixPolicy가 null이면 기본값으로 IF_DEV_THEN_ADD_SUFFIX를 사용한다")
    void constructor_defaultPolicy() {
        // given
        NotificationTarget target = new NotificationTarget("topic", null);

        // then
        assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, target.topicSuffixPolicy());
    }

    @Test
    @DisplayName("topic 정적 팩토리는 기본 suffix 정책을 사용한다")
    void topic_factoryMethod() {
        // given
        NotificationTarget target = NotificationTarget.topic("topic");

        // then
        assertEquals(TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX, target.topicSuffixPolicy());
    }

    @Test
    @DisplayName("topic 정적 팩토리는 전달한 suffix 정책을 유지한다")
    void topic_factoryMethod_withPolicy() {
        // given
        NotificationTarget target = NotificationTarget.topic("topic", TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX);

        // then
        assertEquals(TopicSuffixPolicy.ALWAYS_ADD_DEV_SUFFIX, target.topicSuffixPolicy());
    }
}

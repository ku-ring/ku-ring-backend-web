package com.kustacks.kuring.new_message.domain.model;

import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;

public record NotificationTarget(
        String topic,
        TopicSuffixPolicy topicSuffixPolicy
) {
    public NotificationTarget {
        if (topicSuffixPolicy == null) {
            topicSuffixPolicy = TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX;
        }
    }

    public static NotificationTarget topic(String topic) {
        return new NotificationTarget(topic, TopicSuffixPolicy.IF_DEV_THEN_ADD_SUFFIX);
    }

    public static NotificationTarget topic(String topic, TopicSuffixPolicy policy) {
        return new NotificationTarget(topic, policy);
    }
}



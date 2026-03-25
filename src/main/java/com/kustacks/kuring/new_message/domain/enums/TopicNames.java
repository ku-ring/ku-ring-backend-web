package com.kustacks.kuring.new_message.domain.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TopicNames {
    // 정적 토픽
    public static final String ALL_DEVICE_SUBSCRIBED_TOPIC = "allDevice";
    public static final String ACADEMIC_EVENT_TOPIC = "academicEvent";

    // 동적 토픽
    public static final String CLUB_PREFIX = "club.";
    public static String clubTopic(Long clubId) {
        return CLUB_PREFIX + clubId;
    }

    // 카테고리 이름 기반 토픽
    public static String noticeTopic(String category) {
        return category;
    }
}

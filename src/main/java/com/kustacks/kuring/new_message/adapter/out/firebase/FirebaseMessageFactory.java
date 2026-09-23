package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FirebaseMessageFactory {

    public static Message create(
            String topic,
            String type,
            String title,
            String body,
            Map<String, String> extraData
    ) {
        return baseBuilder(topic, title, body)
                .putAllData(buildData(type, title, body, extraData))
                .build();
    }

    private static Message.Builder baseBuilder(String topic, String title, String body) {
        return Message.builder()
                .setTopic(topic)
                .setNotification(buildNotification(title, body))
                .setApnsConfig(buildApnsConfig());
    }

    private static Notification buildNotification(String title, String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    private static ApnsConfig buildApnsConfig() {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setMutableContent(true)
                        .build())
                .build();
    }

    private static Map<String, String> buildData(
            String type,
            String title,
            String body,
            Map<String, String> extraData
    ) {
        Map<String, String> data = new HashMap<>();
        data.put("type", type);
        data.put("title", title);
        data.put("body", body);

        if (extraData != null && !extraData.isEmpty()) {
            data.putAll(extraData);
        }

        return data;
    }
}

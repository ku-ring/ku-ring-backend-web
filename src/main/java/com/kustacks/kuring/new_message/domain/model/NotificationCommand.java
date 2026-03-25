package com.kustacks.kuring.new_message.domain.model;

import com.kustacks.kuring.new_message.domain.enums.MessageType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public record NotificationCommand(
        NotificationTarget target,
        NotificationContent content,
        MessageType messageType,
        Map<String, String> data
) {
    public NotificationCommand {
        if (data == null) {
            data = Collections.emptyMap();
        } else {
            data = Map.copyOf(data);
        }
    }
    public Map<String, String> mergedData() {
        Map<String, String> merged = new HashMap<>(data);
        merged.putIfAbsent("title", content.title());
        merged.putIfAbsent("body", content.body());
        merged.putIfAbsent("messageType", messageType.getValue());
        return merged;
    }
}

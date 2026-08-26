package com.kustacks.kuring.new_message.domain.model;

import com.kustacks.kuring.new_message.domain.enums.MessageType;

import java.util.Collections;
import java.util.Map;

public record NotificationCommand(
        NotificationTarget target,
        NotificationContent content,
        MessageType messageType,
        Map<String, String> data
) {

    public NotificationCommand {
        if(target == null || content == null || messageType == null)
            throw new IllegalArgumentException("target, content, messageType은 필수입니다.");

        if (data == null) {
            data = Collections.emptyMap();
        } else {
            data = Map.copyOf(data);
        }
    }

}

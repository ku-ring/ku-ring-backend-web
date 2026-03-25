package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.domain.enums.TopicSuffixPolicy;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;

import com.kustacks.kuring.new_message.exception.message.MessageSendException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class FirebasePushMessageAdapter implements PushMessagePort {

    private final FirebaseMessaging firebaseMessaging;
    private final ServerProperties serverProperties;

    @Override
    public void send(NotificationCommand command) throws MessageSendException {
        try {
            firebaseMessaging.send(toMessage(command));
        } catch (FirebaseMessagingException e) {
            throw new MessageSendException();
        }

    }

    private Message toMessage(NotificationCommand command) {
        return Message.builder()
                .setTopic(resolveTopic(command.target().topic(), command.target().topicSuffixPolicy()))
                .setNotification(Notification.builder()
                        .setTitle(command.content().title())
                        .setBody(command.content().body())
                        .build())
                .putAllData(command.mergedData())
                .build();
    }

    private String resolveTopic(String topic, TopicSuffixPolicy policy) {
        return switch (policy) {
            case IF_DEV_THEN_ADD_SUFFIX -> serverProperties.ifDevThenAddSuffix(topic);
            case ALWAYS_ADD_DEV_SUFFIX -> serverProperties.addDevSuffix(topic);
        };
    }

}

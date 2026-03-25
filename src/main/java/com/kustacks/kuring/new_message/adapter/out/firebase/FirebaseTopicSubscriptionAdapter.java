package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;

import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;

import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class FirebaseTopicSubscriptionAdapter implements TopicSubscriptionPort {

    private final FirebaseMessaging firebaseMessaging;
    private final ServerProperties serverProperties;

    @Override
    public void subscribe(String token, String topic) throws MessageSubscribeException{
        try {
            firebaseMessaging.subscribeToTopic(
                    List.of(token),
                    serverProperties.ifDevThenAddSuffix(topic)
            );
        } catch (FirebaseMessagingException e) {
            throw new MessageSubscribeException();
        }
    }

    @Override
    public void unsubscribe(String token, String topic) throws MessageUnSubscribeException {
        try {
            firebaseMessaging.unsubscribeFromTopic(
                    List.of(token),
                    serverProperties.ifDevThenAddSuffix(topic)
            );
        } catch (FirebaseMessagingException e) {
            throw new MessageUnSubscribeException();
        }
    }
}

package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;

import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;

import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Slf4j
@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class FirebaseSubscriptionAdapter implements TopicSubscriptionPort, DeviceTokenValidationPort {

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
            throw new MessageSubscribeException(e);
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
            throw new MessageUnSubscribeException(e);
        }
    }

    @Override
    public boolean validate(String token) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .build();
            firebaseMessaging.send(message, true);
            return true;
        } catch (Exception e) {
            log.warn("유효하지 않은 FCM 토큰입니다.", e);
            return false;
        }
    }
}

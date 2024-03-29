package com.kustacks.kuring.message.application.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserUnsubscribeCommand;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class FirebaseSubscribeService implements FirebaseWithUserUseCase {

    public static final String ALL_DEVICE_SUBSCRIBED_TOPIC = "allDevice";

    private final FirebaseSubscribePort firebaseSubscribePort;
    private final FirebaseMessagingPort firebaseMessagingPort;
    private final ServerProperties serverProperties;

    @Override
    public void validationToken(String token) throws FirebaseInvalidTokenException {
        try {
            Message message = Message.builder().setToken(token).build();
            firebaseMessagingPort.send(message);
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInvalidTokenException();
        }
    }

    @Override
    public void subscribe(UserSubscribeCommand command) throws FirebaseSubscribeException {
        try {
            firebaseSubscribePort.subscribeToTopic(
                    List.of(command.token()),
                    serverProperties.ifDevThenAddSuffix(command.topic())
            );
        } catch (FirebaseSubscribeException exception) {
            throw new FirebaseSubscribeException();
        }
    }

    @Override
    public void unsubscribe(UserUnsubscribeCommand command) throws FirebaseUnSubscribeException {
        try {
            firebaseSubscribePort.unsubscribeFromTopic(
                    List.of(command.token()),
                    serverProperties.ifDevThenAddSuffix(command.topic())
            );
        } catch (FirebaseUnSubscribeException exception) {
            throw new FirebaseUnSubscribeException();
        }
    }
}

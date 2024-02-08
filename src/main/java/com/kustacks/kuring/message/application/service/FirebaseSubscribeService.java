package com.kustacks.kuring.message.application.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.port.in.dto.UserSubscribeCommand;
import com.kustacks.kuring.message.application.port.in.dto.UserUnsubscribeCommand;
import com.kustacks.kuring.message.application.port.out.FirebaseAuthPort;
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
    private final FirebaseAuthPort firebaseAuthPort;
    private final ServerProperties serverProperties;

    @Override
    public void validationToken(String token) throws FirebaseInvalidTokenException {
        try {
            firebaseAuthPort.verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new FirebaseInvalidTokenException();
        }
    }

    @Override
    public void subscribe(UserSubscribeCommand command) throws FirebaseSubscribeException {
        try {
            TopicManagementResponse response = firebaseSubscribePort
                    .subscribeToTopic(
                            List.of(command.token()),
                            serverProperties.ifDevThenAddSuffix(command.topic())
                    );

            if (response.getFailureCount() > 0) {
                throw new FirebaseSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseSubscribeException exception) {
            throw new FirebaseSubscribeException();
        }
    }

    @Override
    public void unsubscribe(UserUnsubscribeCommand command) throws FirebaseUnSubscribeException {
        try {
            TopicManagementResponse response = firebaseSubscribePort
                    .unsubscribeFromTopic(List.of(command.token()), serverProperties.ifDevThenAddSuffix(command.topic()));

            if (response.getFailureCount() > 0) {
                throw new FirebaseUnSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseUnSubscribeException exception) {
            throw new FirebaseUnSubscribeException();
        }
    }
}

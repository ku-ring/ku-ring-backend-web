package com.kustacks.kuring.message.adapter.out.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.message.application.port.out.FirebaseAuthPort;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class FirebaseAdapter implements FirebaseSubscribePort, FirebaseAuthPort, FirebaseMessagingPort {

    private final FirebaseMessaging firebaseMessaging;
    private final FirebaseAuth firebaseAuth;

    @Override
    public void verifyIdToken(String idToken) throws FirebaseInvalidTokenException {
        try {
            firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException exception) {
            throw new FirebaseInvalidTokenException();
        }
    }

    @Override
    public void subscribeToTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseSubscribeException {
        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(tokens, topic);

            if (response.getFailureCount() > 0) {
                throw new FirebaseSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseSubscribeException exception) {
            throw new FirebaseSubscribeException();
        }
    }

    @Override
    public void unsubscribeFromTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseUnSubscribeException {
        try {
            TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(tokens, topic);

            if (response.getFailureCount() > 0) {
                throw new FirebaseUnSubscribeException();
            }
        } catch (FirebaseMessagingException | FirebaseUnSubscribeException exception) {
            throw new FirebaseUnSubscribeException();
        }
    }

    @Override
    public void send(Message message) throws FirebaseMessagingException {
        firebaseMessaging.send(message);
    }
}

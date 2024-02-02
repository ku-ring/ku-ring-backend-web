package com.kustacks.kuring.message.adapter.out.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import com.kustacks.kuring.message.application.port.out.FirebaseAuthPort;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseAdapter implements FirebaseSubscribePort, FirebaseAuthPort, FirebaseMessagingPort {

    private final FirebaseMessaging firebaseMessaging;
    private final FirebaseAuth firebaseAuth;

    @Override
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return firebaseAuth.verifyIdToken(idToken);
    }

    @Override
    public TopicManagementResponse subscribeToTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseMessagingException {
        return firebaseMessaging.subscribeToTopic(tokens, topic);
    }

    @Override
    public TopicManagementResponse unsubscribeFromTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseMessagingException {
        return firebaseMessaging.unsubscribeFromTopic(tokens, topic);
    }

    @Override
    public String send(Message message) throws FirebaseMessagingException {
        return firebaseMessaging.send(message);
    }
}

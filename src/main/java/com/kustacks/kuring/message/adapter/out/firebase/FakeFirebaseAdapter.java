package com.kustacks.kuring.message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.kustacks.kuring.message.application.port.out.FirebaseAuthPort;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import com.kustacks.kuring.message.application.port.out.FirebaseSubscribePort;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("local | test")
@RequiredArgsConstructor
public class FakeFirebaseAdapter implements FirebaseSubscribePort, FirebaseAuthPort, FirebaseMessagingPort {

    @Override
    public void verifyIdToken(String idToken) throws FirebaseInvalidTokenException {
        log.info("FirebaseAdapter.verifyIdToken()");
    }

    @Override
    public void subscribeToTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseSubscribeException {
        log.info("FirebaseAdapter.subscribeToTopic()");
    }

    @Override
    public void unsubscribeFromTopic(
            List<String> tokens,
            String topic
    ) throws FirebaseUnSubscribeException {
        log.info("FirebaseAdapter.unsubscribeFromTopic()");
    }

    @Override
    public void send(Message message) throws FirebaseMessagingException {
        log.info("FirebaseAdapter.send()");
    }
}

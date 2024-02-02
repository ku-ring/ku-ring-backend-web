package com.kustacks.kuring.message.application.port.out;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;

import java.util.List;

public interface FirebaseSubscribePort {
    TopicManagementResponse subscribeToTopic(List<String> tokens, String topic) throws FirebaseMessagingException;
    TopicManagementResponse unsubscribeFromTopic(List<String> tokens, String topic) throws FirebaseMessagingException;
}

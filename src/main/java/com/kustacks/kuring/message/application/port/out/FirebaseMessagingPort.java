package com.kustacks.kuring.message.application.port.out;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

public interface FirebaseMessagingPort {
    String send(Message message) throws FirebaseMessagingException;
}

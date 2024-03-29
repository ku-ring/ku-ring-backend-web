package com.kustacks.kuring.message.application.port.out;

import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;

import java.util.List;

public interface FirebaseSubscribePort {
    void subscribeToTopic(List<String> tokens, String topic) throws FirebaseSubscribeException;
    void unsubscribeFromTopic(List<String> tokens, String topic) throws FirebaseUnSubscribeException;
}

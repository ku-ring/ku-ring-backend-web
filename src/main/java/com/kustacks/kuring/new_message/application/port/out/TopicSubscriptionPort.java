package com.kustacks.kuring.new_message.application.port.out;

import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;

public interface TopicSubscriptionPort {

    void subscribe(String token, String topic) throws MessageSubscribeException;

    void unsubscribe(String token, String topic) throws MessageUnSubscribeException;

}

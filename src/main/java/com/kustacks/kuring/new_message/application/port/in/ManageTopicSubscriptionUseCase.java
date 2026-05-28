package com.kustacks.kuring.new_message.application.port.in;

public interface ManageTopicSubscriptionUseCase {

    void subscribe(String token, String topic);

    void unsubscribe(String token, String topic);

}

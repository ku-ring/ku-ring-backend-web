package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;
import com.kustacks.kuring.new_message.application.port.out.PushMessagePort;
import com.kustacks.kuring.new_message.application.port.out.TopicSubscriptionPort;
import com.kustacks.kuring.new_message.domain.model.NotificationCommand;
import com.kustacks.kuring.new_message.exception.message.MessageSendException;
import com.kustacks.kuring.new_message.exception.message.MessageSubscribeException;
import com.kustacks.kuring.new_message.exception.message.MessageUnSubscribeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local | test")
@RequiredArgsConstructor
public class NewFakeFirebaseAdapter implements
        DeviceTokenValidationPort,
        PushMessagePort,
        TopicSubscriptionPort {

    @Override
    public boolean validate(String token) {
        log.info("NewFakeFirebaseAdapter.validate() token={}", token);
        return true;
    }

    @Override
    public void send(NotificationCommand command) throws MessageSendException {
        log.info(
                "NewFakeFirebaseAdapter.send() topic={}, title={}, body={}",
                command.target().topic(),
                command.content().title(),
                command.content().body()
        );
    }

    @Override
    public void subscribe(String token, String topic) throws MessageSubscribeException {
        log.info("NewFakeFirebaseAdapter.subscribe() token={}, topic={}", token, topic);
    }

    @Override
    public void unsubscribe(String token, String topic) throws MessageUnSubscribeException {
        log.info("NewFakeFirebaseAdapter.unsubscribe() token={}, topic={}", token, topic);
    }
}

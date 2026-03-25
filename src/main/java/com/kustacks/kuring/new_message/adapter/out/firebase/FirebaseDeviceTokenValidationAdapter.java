package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("prod | dev")
@RequiredArgsConstructor
public class FirebaseDeviceTokenValidationAdapter implements DeviceTokenValidationPort {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public boolean validate(String token) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .build();
            firebaseMessaging.send(message, true);
            return true;
        } catch (Exception e) {
            log.warn("유효하지 않은 FCM 토큰입니다.", e);
            return false;
        }
    }
}

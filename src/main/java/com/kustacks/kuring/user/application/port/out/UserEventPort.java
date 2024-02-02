package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;

public interface UserEventPort {
    // TODO : 이게 이벤트여야 하는가?
    void validationTokenEvent(String token) throws FirebaseInvalidTokenException;

    void subscribeEvent(String token, String topic) throws FirebaseSubscribeException;

    void unsubscribeEvent(String token, String topic) throws FirebaseUnSubscribeException;
}

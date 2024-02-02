package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.message.application.service.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.application.service.exception.FirebaseUnSubscribeException;

public interface UserEventPort {

    void subscribeEvent(String token, String topic) throws FirebaseSubscribeException;

    void unsubscribeEvent(String token, String topic) throws FirebaseUnSubscribeException;
}

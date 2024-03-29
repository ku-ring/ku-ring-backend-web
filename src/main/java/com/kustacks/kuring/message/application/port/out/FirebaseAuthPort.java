package com.kustacks.kuring.message.application.port.out;

import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;

public interface FirebaseAuthPort {
    void verifyIdToken(String idToken) throws FirebaseInvalidTokenException;
}

package com.kustacks.kuring.message.application.port.out;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public interface FirebaseAuthPort {
    FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException;
}

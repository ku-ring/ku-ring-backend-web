package com.kustacks.kuring.new_message.adapter.out.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseDeviceTokenValidationAdapterTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FirebaseDeviceTokenValidationAdapter firebaseDeviceTokenValidationAdapter;

    @Test
    @DisplayName("dry-run 전송에 성공하면 true를 반환한다")
    void validate_success() throws Exception {
        // given
        String token = "valid-token";

        // when
        boolean result = firebaseDeviceTokenValidationAdapter.validate(token);

        // then
        assertAll(
                () -> assertTrue(result),
                () -> verify(firebaseMessaging).send(any(Message.class), eq(true))
        );
    }

    @Test
    @DisplayName("dry-run 전송 중 예외가 발생하면 false를 반환한다")
    void validate_fail() throws Exception {
        // given
        String token = "invalid-token";
        when(firebaseMessaging.send(any(Message.class), eq(true)))
                .thenThrow(new RuntimeException("invalid token"));

        // when
        boolean result = firebaseDeviceTokenValidationAdapter.validate(token);

        // then
        assertAll(
                () -> assertFalse(result),
                () -> verify(firebaseMessaging).send(any(Message.class), eq(true))
        );
    }
}

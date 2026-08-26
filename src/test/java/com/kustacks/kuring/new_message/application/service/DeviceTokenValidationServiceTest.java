package com.kustacks.kuring.new_message.application.service;

import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenValidationServiceTest {

    @Mock
    DeviceTokenValidationPort port;

    @InjectMocks
    private DeviceTokenValidationService deviceTokenValidationService;

    @Test
    @DisplayName("디바이스 토큰 검증을 포트에 위임한다")
    void validate_success() {
        // given
        String token = "valid-token";
        when(port.validate(token)).thenReturn(true);

        // when
        boolean result = deviceTokenValidationService.validate(token);

        // then
        assertAll(
                () -> assertTrue(result),
                () -> verify(port).validate(token)
        );
    }
}

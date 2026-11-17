package com.kustacks.kuring.new_message.application.service.intergrationTest;

import com.kustacks.kuring.new_message.adapter.out.firebase.NewFakeFirebaseAdapter;
import com.kustacks.kuring.new_message.application.port.out.DeviceTokenValidationPort;
import com.kustacks.kuring.new_message.application.service.DeviceTokenValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = DeviceTokenValidationServiceIntegrationTest.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("토큰 유효성 검사 서비스 통합 테스트")
class DeviceTokenValidationServiceIntegrationTest {

    @Autowired
    private DeviceTokenValidationService deviceTokenValidationService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        DeviceTokenValidationService deviceTokenValidationService(DeviceTokenValidationPort deviceTokenValidationPort) {
            return new DeviceTokenValidationService(deviceTokenValidationPort);
        }

        @Bean
        DeviceTokenValidationPort deviceTokenValidationPort() {
            return new NewFakeFirebaseAdapter();
        }
    }

    @Test
    @DisplayName("FakeFirebaseAdapter와 연동하여 토큰 검증에 성공한다")
    void validate_success() {
        boolean result = deviceTokenValidationService.validate("token");
        assertTrue(result);
    }
}

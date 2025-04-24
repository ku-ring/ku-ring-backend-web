package com.kustacks.kuring.common.utils.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class VerificationCodeGeneratorTest {

    @Test
    @DisplayName("generateVerificationCode 메서드는 6자리 인증 코드를 생성한다")
    void generateVerificationCode_ShouldGenerateSixDigitCode() {
        // when
        String code = VerificationCodeGenerator.generateVerificationCode();

        // then
        assertThat(code).isNotNull();
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }

    @Test
    @DisplayName("generateVerificationCode 메서드는 RandomGenerator를 사용하여 코드를 생성한다")
    void generateVerificationCode_ShouldUseRandomGenerator() {
        try (MockedStatic<RandomGenerator> mockedRandomGenerator = mockStatic(RandomGenerator.class)) {
            // given
            mockedRandomGenerator.when(() -> RandomGenerator.generateRandomNumber(6))
                    .thenReturn("987654");

            // when
            String code = VerificationCodeGenerator.generateVerificationCode();

            // then
            assertThat(code).isEqualTo("987654");

            // 메서드 호출 검증
            mockedRandomGenerator.verify(() -> RandomGenerator.generateRandomNumber(6));
        }
    }
}
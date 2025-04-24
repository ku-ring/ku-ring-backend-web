package com.kustacks.kuring.common.utils.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RandomGeneratorTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 6, 10})
    @DisplayName("generateRandomNumber 메서드는 지정된 길이의 숫자 문자열을 생성한다")
    void generateRandomNumber_ShouldReturnStringWithSpecifiedLength(int length) {
        // when
        String result = RandomGenerator.generateRandomNumber(length);

        // then
        assertThat(result).hasSize(length);
        assertThat(result).matches("\\d{" + length + "}");
    }

    @Test
    @DisplayName("generateRandomNumber 메서드는 길이가 0인 경우 빈 문자열을 반환한다")
    void generateRandomNumber_ShouldReturnEmptyString_WhenLengthIsZero() {
        // when
        String result = RandomGenerator.generateRandomNumber(0);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("generateRandomNumber 메서드는 길이가 음수인 경우 빈 문자열을 반환한다")
    void generateRandomNumber_ShouldReturnEmptyString_WhenLengthIsNegative() {
        // when
        String result = RandomGenerator.generateRandomNumber(-5);

        // then
        assertThat(result).isEmpty();
    }
}
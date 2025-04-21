package com.kustacks.kuring.common.utils.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
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

    @RepeatedTest(100)
    @DisplayName("generatePositiveNumber 메서드는 1 이상 bound 미만의 양수를 생성한다")
    void generatePositiveNumber_ShouldReturnPositiveNumberLessThanMaxValue() {
        // given
        int bound = 10;

        // when
        int result = RandomGenerator.generatePositiveNumber(bound);

        // then
        assertThat(result).isGreaterThanOrEqualTo(1);
        assertThat(result).isLessThan(bound);
    }

    @Test
    @DisplayName("generatePositiveNumber 메서드는 bound가 0이하인 경우 IllegalArgumentException 예외가 발생한다.")
    void generatePositiveNumber_ShouldThrowsException_WhenMaxValueIsLessThanTwo() {
        // given
        int bound = 0;

        // when
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> RandomGenerator.generatePositiveNumber(bound));
    }

    @Test
    @DisplayName("generatePositiveNumber 메서드는 bound가 1인 경우 1응 응답한다.")
    void generatePositiveNumber_ShouldReturnOne_WhenMaxValueIsLessThanTwo() {
        // given
        int bound = 1;

        // when
        assertThat(RandomGenerator.generatePositiveNumber(bound)).isEqualTo(1);
    }
}
package com.kustacks.kuring.common.utils.converter;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringToDateTimeConverterTest {

    @DisplayName("날짜 시간이 yyyy-MM-dd HH:mm:ss 형태로 주어지면 LocalDateTime 으로 변환한다")
    @Test
    void date_time_test() {
        // given
        String stringDate = "2023-04-03 00:00:12";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 4, 3, 0, 0, 12);

        // when
        LocalDateTime convertedDateTime = StringToDateTimeConverter.convert(stringDate);

        // then
        assertThat(convertedDateTime).isEqualTo(localDateTime);
    }

    @DisplayName("지정된 형식의 날짜 시간 구조가 아닌 경우 예외가 발생한다")
    @CsvSource({"2023:04:03", "2023-4-3", "04-03", "-04-03",
            "2023-04-03 00:00", "2023-04-03 00", "2023-04-03 00:00:00:00", "00:00:00", "2023-04-0300:00:12"})
    @ParameterizedTest
    void date_time_convert_fail(String dateTime) {
        // when
        ThrowableAssert.ThrowingCallable actual = () -> StringToDateTimeConverter.convert(dateTime);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class);
    }
}

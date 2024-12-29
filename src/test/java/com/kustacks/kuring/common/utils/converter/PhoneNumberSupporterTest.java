package com.kustacks.kuring.common.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberSupporterTest {

    @DisplayName("전화번호 구조 변경 테스트")
    @Test
    void convertValidNumber() {
        //given
        String[] numbers = new String[]{
                "1234",
                "02-450-1234",
                "02)450-1234",
                "02-2049-1234",
                "02)2049-1234",
                "218)",
                "",
                null};
        String[] answer = new String[]{
                "02-450-1234",
                "02-450-1234",
                "02-450-1234",
                "02-2049-1234",
                "02-2049-1234",
                "",
                "",
                ""};

        //when
        List<String> convertedNumbers = Arrays.stream(numbers)
                .map(PhoneNumberSupporter::convertFullExtensionNumber)
                .toList();

        //then
        for (int i = 0; i < numbers.length; i++) {
            assertThat(Objects.equals(convertedNumbers.get(i), answer[i])).isTrue();
        }
    }

    @DisplayName("전화번호 Null or Blank 테스트")
    @Test
    void validationEmailTest() {
        //given
        String[] numbers = new String[]{
                "1234",
                "02-450-1234",
                "02)450-1234",
                "02-2049-1234",
                "02)2049-1234",
                "218)",
                "",
                null};

        Boolean[] answer = new Boolean[]{false, false, false, false, false, false, true, true};

        //when
        List<Boolean> validationResults = Arrays.stream(numbers)
                .map(PhoneNumberSupporter::isNullOrBlank)
                .toList();

        //then
        for (int i = 0; i < numbers.length; i++) {
            assertThat(Objects.equals(validationResults.get(i), answer[i])).isTrue();
        }
    }
}
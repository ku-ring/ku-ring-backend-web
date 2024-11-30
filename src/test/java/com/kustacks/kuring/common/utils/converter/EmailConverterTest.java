package com.kustacks.kuring.common.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class EmailConverterTest {

    @DisplayName("이메일 구조 변경 테스트")
    @Test
    public void convertValidEmail() {
        //given
        String[] emails = new String[]{
                "ab1234@konkuk.ac.kr",
                "gclee at konkuk dot ac dot kr",
                "slryu2002@konkuk.ac.kr/ slryu2002@gmail.com",
                "sawng@konkuk.ac.kr, ywsong13@gmail.com",
                "",
                null};
        String[] answer = new String[]{
                "ab1234@konkuk.ac.kr",
                "gclee@konkuk.ac.kr",
                "slryu2002@konkuk.ac.kr",
                "sawng@konkuk.ac.kr",
                "",
                ""};

        //when
        List<String> convertedEmails = Arrays.stream(emails)
                .map(EmailConverter::convertValidEmail)
                .toList();

        //then
        for (int i = 0; i < emails.length; i++) {
            assertThat(Objects.equals(convertedEmails.get(i), answer[i])).isTrue();
        }
    }
}
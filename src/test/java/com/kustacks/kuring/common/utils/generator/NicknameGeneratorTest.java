package com.kustacks.kuring.common.utils.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NicknameGeneratorTest {
    private final String[] nicknamePrefixes = {"쿠링이", "건덕이", "건구스"};
    private final String nicknameRegexFormat = "^(%s)\\d{6}$";

    @Test
    @DisplayName("generateNickname 메서드는 닉네임을 생성한다")
    void generateNickname_ShouldGenerateNickname() {
        // when
        String nickname = NicknameGenerator.generateNickname();

        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname).isNotEmpty();

        String prefixRegex = String.join("|", nicknamePrefixes);
        String fullRegex = String.format(nicknameRegexFormat, prefixRegex);

        assertThat(nickname.matches(fullRegex)).isTrue();
    }
}
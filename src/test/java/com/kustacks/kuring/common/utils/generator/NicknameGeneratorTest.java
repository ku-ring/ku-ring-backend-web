package com.kustacks.kuring.common.utils.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NicknameGeneratorTest {
    String[] prefixes = {"쿠링이", "건덕이", "건구스"};

    @Test
    @DisplayName("generateNickname 메서드는 닉네임을 생성한다")
    void generateNickname_ShouldGenerateNickname() {
        // when
        String nickname = NicknameGenerator.generateNickname();

        // then
        assertThat(nickname).isNotNull();
        assertThat(nickname).isNotEmpty();

        boolean isMatch = false;
        // 닉네임 형식 검증: 접두사 + 6자리 숫자
        for (String prefix : prefixes) {
            String regex = prefix + "\\d{6}";
            if (nickname.matches(regex)) {
                isMatch = true;
                break;
            }
        }
        assertThat(isMatch).isTrue();
    }
}
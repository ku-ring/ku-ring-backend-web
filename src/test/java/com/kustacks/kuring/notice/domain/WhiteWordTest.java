package com.kustacks.kuring.notice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("도메인 : WhiteWord")
class WhiteWordTest {

    @Test
    @DisplayName("WhiteWord를 생성할 수 있다")
    void createWhiteWord() {
        // given
        String word = "테스트";
        String description = "테스트 설명";
        Boolean isActive = true;

        // when
        WhiteWord whiteWord = new WhiteWord(word, description, isActive);

        // then
        assertAll(
                () -> assertThat(whiteWord.getWord()).isEqualTo(word),
                () -> assertThat(whiteWord.getDescription()).isEqualTo(description),
                () -> assertThat(whiteWord.getIsActive()).isTrue()
        );
    }

    @Test
    @DisplayName("isActive가 null이면 기본값으로 true가 설정된다")
    void createWhiteWordWithNullIsActive() {
        // given
        String word = "테스트";
        String description = "테스트 설명";

        // when
        WhiteWord whiteWord = new WhiteWord(word, description, null);

        // then
        assertThat(whiteWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("isActive가 false이면 false로 설정된다")
    void createWhiteWordWithFalseIsActive() {
        // given
        String word = "테스트";
        String description = "테스트 설명";
        Boolean isActive = false;

        // when
        WhiteWord whiteWord = new WhiteWord(word, description, isActive);

        // then
        assertThat(whiteWord.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("description이 null이어도 생성할 수 있다")
    void createWhiteWordWithNullDescription() {
        // given
        String word = "테스트";
        String description = null;
        Boolean isActive = true;

        // when
        WhiteWord whiteWord = new WhiteWord(word, description, isActive);

        // then
        assertAll(
                () -> assertThat(whiteWord.getWord()).isEqualTo(word),
                () -> assertThat(whiteWord.getDescription()).isNull(),
                () -> assertThat(whiteWord.getIsActive()).isTrue()
        );
    }

    @Test
    @DisplayName("WhiteWord를 활성화할 수 있다")
    void activateWhiteWord() {
        // given
        WhiteWord whiteWord = new WhiteWord("테스트", "설명", false);

        // when
        whiteWord.activate();

        // then
        assertThat(whiteWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("WhiteWord를 비활성화할 수 있다")
    void deactivateWhiteWord() {
        // given
        WhiteWord whiteWord = new WhiteWord("테스트", "설명", true);

        // when
        whiteWord.deactivate();

        // then
        assertThat(whiteWord.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("이미 활성화된 WhiteWord를 다시 활성화해도 true 상태를 유지한다")
    void activateAlreadyActiveWhiteWord() {
        // given
        WhiteWord whiteWord = new WhiteWord("테스트", "설명", true);

        // when
        whiteWord.activate();

        // then
        assertThat(whiteWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("이미 비활성화된 WhiteWord를 다시 비활성화해도 false 상태를 유지한다")
    void deactivateAlreadyInactiveWhiteWord() {
        // given
        WhiteWord whiteWord = new WhiteWord("테스트", "설명", false);

        // when
        whiteWord.deactivate();

        // then
        assertThat(whiteWord.getIsActive()).isFalse();
    }
} 

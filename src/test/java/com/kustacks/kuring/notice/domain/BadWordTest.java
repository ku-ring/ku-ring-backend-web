package com.kustacks.kuring.notice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("도메인 : BadWord")
class BadWordTest {

    @Test
    @DisplayName("BadWord를 생성할 수 있다")
    void createBadWord() {
        // given
        String word = "테스트";
        BadWordCategory category = BadWordCategory.ADULT;
        String description = "테스트 설명";
        Boolean isActive = true;

        // when
        BadWord badWord = new BadWord(word, category, description, isActive);

        // then
        assertThat(badWord.getWord()).isEqualTo(word);
        assertThat(badWord.getCategory()).isEqualTo(category);
        assertThat(badWord.getDescription()).isEqualTo(description);
        assertThat(badWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("isActive가 null이면 기본값으로 true가 설정된다")
    void createBadWordWithNullIsActive() {
        // given
        String word = "테스트";
        BadWordCategory category = BadWordCategory.ADULT;
        String description = "테스트 설명";

        // when
        BadWord badWord = new BadWord(word, category, description, null);

        // then
        assertThat(badWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("BadWord를 활성화할 수 있다")
    void activateBadWord() {
        // given
        BadWord badWord = new BadWord("테스트", BadWordCategory.ADULT, "설명", false);

        // when
        badWord.activate();

        // then
        assertThat(badWord.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("BadWord를 비활성화할 수 있다")
    void deactivateBadWord() {
        // given
        BadWord badWord = new BadWord("테스트", BadWordCategory.ADULT, "설명", true);

        // when
        badWord.deactivate();

        // then
        assertThat(badWord.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("BadWord의 단어를 수정할 수 있다")
    void updateWord() {
        // given
        BadWord badWord = new BadWord("원래단어", BadWordCategory.ADULT, "설명", true);
        String newWord = "새단어";

        // when
        badWord.updateWord(newWord);

        // then
        assertThat(badWord.getWord()).isEqualTo(newWord);
    }

    @Test
    @DisplayName("BadWord의 설명을 수정할 수 있다")
    void updateDescription() {
        // given
        BadWord badWord = new BadWord("단어", BadWordCategory.ADULT, "원래설명", true);
        String newDescription = "새설명";

        // when
        badWord.updateDescription(newDescription);

        // then
        assertThat(badWord.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("BadWord의 카테고리를 수정할 수 있다")
    void updateCategory() {
        // given
        BadWord badWord = new BadWord("단어", BadWordCategory.ADULT, "설명", true);
        BadWordCategory newCategory = BadWordCategory.DRUG;

        // when
        badWord.updateCategory(newCategory);

        // then
        assertThat(badWord.getCategory()).isEqualTo(newCategory);
    }
}

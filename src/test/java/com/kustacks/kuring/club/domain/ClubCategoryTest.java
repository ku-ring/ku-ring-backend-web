package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("도메인 : ClubCategory")
class ClubCategoryTest {

    @DisplayName("name을 받아 해당 ClubCategory enum으로 변환한다")
    @ParameterizedTest
    @CsvSource({"academic,ACADEMIC", "culture_art,CULTURE_ART", "social_value,SOCIAL_VALUE", "activity,ACTIVITY"})
    void fromName(String name, ClubCategory clubCategory) {
        // when
        ClubCategory result = ClubCategory.fromName(name);

        // then
        assertThat(result).isEqualTo(clubCategory);
    }

    @DisplayName("존재하지 않는 name으로 ClubCategory를 찾으려 하면 예외가 발생한다")
    @Test
    void fromNameException() {
        // given
        String name = "invalid";

        // when
        ThrowingCallable actual = () -> ClubCategory.fromName(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }
}

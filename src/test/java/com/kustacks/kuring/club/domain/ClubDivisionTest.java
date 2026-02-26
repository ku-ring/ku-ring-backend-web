package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("도메인 : ClubDivision")
class ClubDivisionTest {

    @DisplayName("name을 받아 해당 ClubDivision enum으로 변환한다")
    @ParameterizedTest
    @CsvSource({"central,CENTRAL", "engineering,ENGINEERING", "etc,ETC"})
    void fromName(String name, ClubDivision clubDivision) {
        // when
        ClubDivision result = ClubDivision.fromName(name);

        // then
        assertThat(result).isEqualTo(clubDivision);
    }

    @DisplayName("존재하지 않는 name으로 ClubDivision을 찾으려 하면 예외가 발생한다")
    @Test
    void fromNameException() {
        // given
        String name = "invalid";

        // when
        ThrowingCallable actual = () -> ClubDivision.fromName(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }
}

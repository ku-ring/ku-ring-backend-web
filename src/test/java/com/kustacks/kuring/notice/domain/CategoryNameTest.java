package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryNameTest {

    @DisplayName("name을 String으로 받아 해당 CategoryName enum으로 변환한다")
    @CsvSource({"bachelor,BACHELOR", "industry_university,INDUSTRY_UNIVERSITY", "library,LIBRARY"})
    @ParameterizedTest
    public void fromStringName(String name, CategoryName categoryName) {
        // when
        CategoryName result = CategoryName.fromStringName(name);

        // then
        assertThat(result).isEqualTo(categoryName);
    }

    @DisplayName("존재하지 않는 String name으로 CategoryName을 찾으려 하는 경우 예외가 발생한다")
    @Test
    public void fromStringNameException() {
        // given
        String name = "invalidName";

        // when
        ThrowingCallable actual = () -> CategoryName.fromStringName(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("String name을 통해 동일한 CategoryName enum인지 확인한다")
    @CsvSource({"bachelor,true", "invalid,false"})
    @ParameterizedTest
    public void isSameName(String name, boolean expected) {
        // when
        boolean result = CategoryName.BACHELOR.isSameName(name);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("String shortName을 통해 동일한 CategoryName enum인지 확인한다")
    @CsvSource({"bch,true", "invalid,false"})
    @ParameterizedTest
    public void isSameShortName(String name, boolean expected) {
        // when
        boolean result = CategoryName.BACHELOR.isSameShortName(name);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("String korName을 통해 동일한 CategoryName enum인지 확인한다")
    @CsvSource({"학사,true", "invalid,false"})
    @ParameterizedTest
    public void isSameKorName(String name, boolean expected) {
        // when
        boolean result = CategoryName.BACHELOR.isSameKorName(name);

        // then
        assertThat(result).isEqualTo(expected);
    }
}

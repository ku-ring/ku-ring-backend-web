package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepartmentNameTest {

    @DisplayName("name을 String으로 받아 해당 DepartmentName enum으로 변환한다")
    @CsvSource({"korean,KOREAN", "civil_environment,CIVIL_ENV", "business_administration,BUIS_ADMIN"})
    @ParameterizedTest
    void fromName(String name, DepartmentName departmentName) {
        // when
        DepartmentName result = DepartmentName.fromName(name);

        // then
        assertThat(result).isEqualTo(departmentName);
    }

    @DisplayName("hostPrefix를 String으로 받아 해당 DepartmentName enum으로 변환한다")
    @CsvSource({"korea,KOREAN", "cee,CIVIL_ENV", "biz,BUIS_ADMIN"})
    @ParameterizedTest
    void fromHostPrefix(String name, DepartmentName departmentName) {
        // when
        DepartmentName result = DepartmentName.fromHostPrefix(name);

        // then
        assertThat(result).isEqualTo(departmentName);
    }

    @DisplayName("korName를 String으로 받아 해당 DepartmentName enum으로 변환한다")
    @CsvSource({"국어국문학과,KOREAN", "사회환경공학부,CIVIL_ENV", "경영학과,BUIS_ADMIN"})
    @ParameterizedTest
    void fromKor(String name, DepartmentName departmentName) {
        // when
        DepartmentName result = DepartmentName.fromKor(name);

        // then
        assertThat(result).isEqualTo(departmentName);
    }

    @DisplayName("존재하지 않는 String name으로 DepartmentName을 찾으려 하는 경우 예외가 발생한다")
    @Test
    void fromNameException() {
        // given
        String name = "invalidName";

        // when
        ThrowingCallable actual = () -> DepartmentName.fromName(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 String fromHostPrefix로 DepartmentName을 찾으려 하는 경우 예외가 발생한다")
    @Test
    void fromHostPrefixException() {
        // given
        String name = "invalidName";

        // when
        ThrowingCallable actual = () -> DepartmentName.fromHostPrefix(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 String fromKor로 DepartmentName을 찾으려 하는 경우 예외가 발생한다")
    @Test
    void fromKorException() {
        // given
        String name = "invalidName";

        // when
        ThrowingCallable actual = () -> DepartmentName.fromKor(name);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundException.class);
    }
}

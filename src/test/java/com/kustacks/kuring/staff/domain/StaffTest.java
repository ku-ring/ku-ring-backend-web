package com.kustacks.kuring.staff.domain;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("도메인 : Staff")
class StaffTest {

    @DisplayName("Staff 생성 테스트")
    @Test
    void creat_staff() {
        assertThatCode(() -> Staff.builder()
                .name("name")
                .major("major")
                .lab("lab")
                .phone("02-123-4567")
                .email("email@gmail.com")
                .dept("dept")
                .college("이과대학")
                .build()
        ).doesNotThrowAnyException();
    }

    @DisplayName("Staff의 소속 대학이 존재하지 않는 경우 예외가 발생한다")
    @Test
    void creat_staff_valid_college() {
        // given
        String college = "없는대학교";

        // when
        ThrowingCallable actual = () -> Staff.builder()
                .name("name")
                .major("major")
                .lab("lab")
                .phone("02-123-4567")
                .email("email@gmail.com")
                .dept("dept")
                .college(college)
                .build();

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("교직원의 email 주소가 올바르다면 성공적으로 Staff를 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"shine@gmail.com", "csseo@konkuk.ac.kr", "sjkwon@konkuk.ac.kr"})
    void staff_email(String email) {
        assertThatCode(() -> Staff.builder()
                .name("name")
                .major("major")
                .lab("lab")
                .phone("02-123-4567")
                .email(email)
                .dept("dept")
                .college("이과대학")
                .build()
        ).doesNotThrowAnyException();
    }

    @DisplayName("비정상 적인 email형태로는 Staff를 생성할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"@gmail.com", "csseo", "sjkwon@", "shinegmail.com"})
    void invalid_length_feedback(String email) {
        // when
        ThrowingCallable actual = () -> Staff.builder()
                .name("name")
                .major("major")
                .lab("lab")
                .phone("02-123-4567")
                .email(email)
                .dept("dept")
                .college("이과대학")
                .build();

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 이메일 형식이 아닙니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalid_name(String name) {
        // when
        ThrowingCallable actual = () -> Staff.builder()
                .name(name)
                .major("major")
                .lab("lab")
                .phone("02-123-4567")
                .email("email@gmail.com")
                .dept("dept")
                .college("이과대학")
                .build();

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 필수 입력값입니다.");
    }

    @DisplayName("여러 형태의 이름을 저장할 수 있다")
    @ParameterizedTest
    @MethodSource("nameInputProvider")
    void name_convert(String input, String expected) {
        // when
        Name name = new Name(input);

        // then
        assertThat(name.getValue()).isEqualTo(expected);
    }

    @DisplayName("여러 형태의 전화번호를 통일된 형태로 변환할 수 있다.")
    @ParameterizedTest
    @MethodSource("phoneNumberInputProvider")
    void phone_number_convert(String phone, String expected) {
        // when
        Phone phoneNumber = new Phone(phone);

        // then
        assertThat(phoneNumber.getValue()).isEqualTo(expected);
    }

    private static Stream<Arguments> nameInputProvider() {
        return Stream.of(
                Arguments.of("오인하", "오인하"),
                Arguments.of("정환", "정환"),
                Arguments.of("박세실리아", "박세실리아"),
                Arguments.of("임 걸", "임 걸"),
                Arguments.of("임 준 ( Jun Lim,林 俊 )", "임 준 ( Jun Lim,林 俊 )"),
                Arguments.of("장준 ( ZHANG JUN,張俊 )", "장준 ( ZHANG JUN,張俊 )"),
                Arguments.of("비제이싱", "비제이싱"),
                Arguments.of("나가시마 노리코 ( 長島 倫子 )", "나가시마 노리코 ( 長島 倫子 )"),
                Arguments.of("권성중 ( Kwon Seong Jung,權晟重 )", "권성중 ( Kwon Seong Jung,權晟重 )"),
                Arguments.of("추 프랑솨(진교) ( Choo Francois(Jinkyo),秋鎭敎 )", "추 프랑솨(진교) ( Choo Francois(Jinkyo),秋鎭敎 )"),
                Arguments.of("Kingman Cheng ( Kingman Cheng )", "Kingman Cheng ( Kingman Cheng )"),
                Arguments.of("피터 라이언 ( Peter Andrew Ryan )", "피터 라이언 ( Peter Andrew Ryan )"),
                Arguments.of("Eleanor E. B. Campbe ( Eleanor E. B. Campbell )", "Eleanor E. B. Campbe ( Eleanor E. B. Campbell )")
        );
    }

    private static Stream<Arguments> phoneNumberInputProvider() {
        return Stream.of(
                Arguments.of("02-450-3530", "02-450-3530"),
                Arguments.of(" 02-450-3530", "02-450-3530"),
                Arguments.of("02) 450-0454", "02-450-0454"),
                Arguments.of(" 02) 450-0454", "02-450-0454"),
                Arguments.of("02)450-0454", "02-450-0454"),
                Arguments.of("02) 450 - 3767", "02-450-3767"),
                Arguments.of("450-4176", "02-450-4176"),
                Arguments.of(" 450-4176", "02-450-4176"),
                Arguments.of("450 4176", "02-450-4176"),
                Arguments.of(" 450 4176", "02-450-4176"),
                Arguments.of("02) 2049 - 6017", "02-2049-6017"),
                Arguments.of("02-2049-6052 / 02-457-1341(Lab)", "02-2049-6052 / 02-457-1341(Lab)"),
                Arguments.of("02-450-3936 ( FAX : 02-3437-8360)", "02-450-3936 ( FAX : 02-3437-8360)"),
                Arguments.of("1-505-667-2716", "1-505-667-2716"),
                Arguments.of(" ", ""),
                Arguments.of("", ""),
                Arguments.of(null, "")
        );
    }
}

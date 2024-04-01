package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.InternalLogicException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoticeDateTimeTest {

    @DisplayName("날짜 시간이 있는 경우 내부적으로 yyyy-MM-dd HH:mm:ss 형태를 생성하고 반환시에는 yyyy-MM-dd 형태로 반환한다")
    @Test
    void date_time_test() {
        // given
        String postedDate = "2023-04-03 00:00:12";
        String updatedDate = "2023-04-04 00:00:12";

        // when
        NoticeDateTime postDateTime = new NoticeDateTime(postedDate, updatedDate);

        // then
        assertAll(
                () -> assertThat(postDateTime.postedDateStr()).isEqualTo("2023-04-03"),
                () -> assertThat(postDateTime.updatedDateStr()).isEqualTo("2023-04-04")
        );
    }

    @DisplayName("날짜만 있는 경우에도 시간을 현재 시분초로 설정하여 내부적으로 yyyy-MM-dd HH:mm:ss 형태를 생성하고 반환시 yyyy-MM-dd 형태로 반환한다")
    @Test
    void date_test() {
        // when
        NoticeDateTime postDateTime = new NoticeDateTime("2023-04-03", "2023-04-03");

        // then
        assertAll(
                () -> assertThat(postDateTime.postedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}"),
                () -> assertThat(postDateTime.updatedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}")
        );
    }

    @DisplayName("날짜만 yyyy.MM.dd 처럼 있는 경우에도 내부적으로 yyyy-MM-dd HH:mm:ss 형태를 생성하고 반환시 yyyy-MM-dd 형태로 반환한다")
    @Test
    void date_dot_test() {
        // when
        NoticeDateTime postDateTime = new NoticeDateTime("2023.04.03", "2023.04.03");

        // then
        assertAll(
                () -> assertThat(postDateTime.postedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}"),
                () -> assertThat(postDateTime.updatedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}")
        );
    }

    @DisplayName("업데이트 날자가 공지에 없어 null인 경우에도 내부적으로 yyyy-MM-dd HH:mm:ss 형태를 생성하고 반환시 yyyy-MM-dd 형태로 반환한다")
    @NullAndEmptySource
    @ParameterizedTest
    void date_both_null_test(String dateTime) {
        // when
        NoticeDateTime postDateTime = new NoticeDateTime(dateTime, dateTime);

        // then
        assertAll(
                () -> assertThat(postDateTime.postedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}"),
                () -> assertThat(postDateTime.updatedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}")
        );
    }

    @DisplayName("업데이트 날자가 공지에 없어 null인 경우에도 내부적으로 yyyy-MM-dd HH:mm:ss 형태를 생성하고 반환시 yyyy-MM-dd 형태로 반환한다")
    @Test
    void update_date_null_test() {
        // when
        NoticeDateTime postDateTime = new NoticeDateTime("2023.04.03", null);

        // then
        assertAll(
                () -> assertThat(postDateTime.postedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}"),
                () -> assertThat(postDateTime.updatedDateStr())
                        .containsPattern("^\\d{4}-\\d{2}-\\d{2}")
        );
    }

    @DisplayName("지정된 형식의 날짜 구조가 아닌 경우 예외가 발생한다")
    @CsvSource({"2023:04:03", "2023-4-3", "04-03", "-04-03",
            "2023-04-03 00:00", "2023-04-03 00", "2023-04-03 00:00:00:00", "00:00:00"})
    @ParameterizedTest
    void fromName(String dateTime) {
        // when
        ThrowableAssert.ThrowingCallable actual = () -> new NoticeDateTime(dateTime, dateTime);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }
}

package com.kustacks.kuring.alert.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("도메인 : Alert")
class AlertTest {

    LocalDateTime now;
    LocalDateTime wakeTime;

    @BeforeEach
    void setUp() {
        // given
        now = LocalDateTime.of(2024, 1, 19, 17, 27, 5);
        wakeTime = now.plusMinutes(10);
    }

    @DisplayName("Alert 예약시간은 현 시간보다 미래여야 한다")
    @Test
    void creat_alert() {
        // when, then
        assertThatCode(() -> Alert.createIfValidAlertTime("title", "contents", wakeTime, now))
                .doesNotThrowAnyException();
    }

    @DisplayName("Alert 예약시간이 현 시간과 같거나 과거인 경우 예외를 발생시킨다")
    @Test
    void exception_alert() {
        // when
        ThrowableAssert.ThrowingCallable actual = () -> Alert.createIfValidAlertTime("title", "contents", now, now);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Alert 의 상태를 변경한다")
    @Test
    void change_alert_status() {
        // given
        Alert alert = Alert.createIfValidAlertTime("title", "contents", wakeTime, now);

        // when
        alert.changeCompleted();

        // then
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.COMPLETED);
    }

    @DisplayName("이미 requested 상태의 Alert 상태를 변경하려 하는 경우 예외가 발생한다")
    @Test
    void change_alert_status_exception() {
        // given
        Alert alert = Alert.createIfValidAlertTime("title", "contents", wakeTime, now);
        alert.changeCompleted();

        // when
        ThrowableAssert.ThrowingCallable actual = () -> alert.changeCompleted();

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalStateException.class);
    }
}

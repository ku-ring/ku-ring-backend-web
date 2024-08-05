package com.kustacks.kuring.alert.application.service;

import com.kustacks.kuring.alert.adapter.out.persistence.AlertRepository;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AlertServiceTest extends IntegrationTestSupport {

    @Autowired
    AlertService alertService;

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    Clock clock;

    @DisplayName("알림을 성공적으로 등록한다")
    @Test
    void creat_alert() {
        // given
        LocalDateTime expiredTime = LocalDateTime.now(clock).plus(1, ChronoUnit.SECONDS);
        AlertCreateCommand alertCreateCommand = new AlertCreateCommand(
                "title", "content",
                expiredTime
        );

        // when
        alertService.addAlertSchedule(alertCreateCommand);

        // then
        List<Alert> alertList = alertRepository.findAllByStatus(AlertStatus.PENDING);
        assertAll(
                () -> assertThat(alertList).hasSize(1),
                () -> assertThat(alertList.get(0).getTitle()).isEqualTo("title"),
                () -> assertThat(alertList.get(0).getContent()).isEqualTo("content"),
                () -> assertThat(alertList.get(0).getAlertTime().truncatedTo(ChronoUnit.MICROS))
                        .isEqualTo(expiredTime.truncatedTo(ChronoUnit.MICROS))
        );
    }

    @DisplayName("알림을 성공적으로 취소한다")
    @Test
    void cancel_alert() {
        // given
        LocalDateTime expiredTime = LocalDateTime.now(clock).plus(1, ChronoUnit.SECONDS);
        AlertCreateCommand alertCreateCommand = new AlertCreateCommand(
                "title", "content",
                expiredTime
        );
        alertService.addAlertSchedule(alertCreateCommand);
        Long alertId = alertRepository.findAllByStatus(AlertStatus.PENDING).get(0).getId();

        // when
        alertService.cancelAlertSchedule(alertId);

        // then
        List<Alert> alertList = alertRepository.findAllByStatus(AlertStatus.CANCELED);
        assertAll(
                () -> assertThat(alertList).hasSize(1),
                () -> assertThat(alertList.get(0).getTitle()).isEqualTo("title"),
                () -> assertThat(alertList.get(0).getContent()).isEqualTo("content"),
                () -> assertThat(alertList.get(0).getAlertTime().truncatedTo(ChronoUnit.MICROS))
                        .isEqualTo(expiredTime.truncatedTo(ChronoUnit.MICROS))
        );
    }

    @DisplayName("알림이 울린 후 성공적으로 상태를 변경한다")
    @Test
    void alert_change_status() throws InterruptedException {
        // given
        LocalDateTime expiredTime = LocalDateTime.now(clock).plus(1, ChronoUnit.SECONDS);
        AlertCreateCommand alertCreateCommand = new AlertCreateCommand(
                "title", "content",
                expiredTime
        );
        alertService.addAlertSchedule(alertCreateCommand);

        // when : 1초 기다린 후 알림 상태를 확인합니다.
        Thread.sleep(1500);

        // then
        List<Alert> alertList = alertRepository.findAllByStatus(AlertStatus.COMPLETED);
        assertThat(alertList).hasSize(1);
    }
}

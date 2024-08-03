package com.kustacks.kuring.alert.application.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.alert.adapter.out.persistence.AlertRepository;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;

class AlertServiceTest extends IntegrationTestSupport {

    @Autowired
    AlertService alertService;

    @Autowired
    AlertRepository alertRepository;

    @DisplayName("알림을 성공적으로 등록한다")
    @Test
    void creat_alert() throws FirebaseMessagingException {
        // given
        LocalDateTime expiredTime = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        AlertCreateCommand alertCreateCommand = new AlertCreateCommand(
                "title", "content",
                expiredTime
        );

        // when
        alertService.addAlertSchedule(alertCreateCommand);

        // then
        List<Alert> alertList = alertRepository.findAllByStatus(AlertStatus.PENDING);
        assertAll(
                () -> Assertions.assertThat(alertList).hasSize(1),
                () -> Assertions.assertThat(alertList.get(0).getTitle()).isEqualTo("title"),
                () -> Assertions.assertThat(alertList.get(0).getContent()).isEqualTo("content"),
                () -> Assertions.assertThat(alertList.get(0).getAlertTime()).isEqualTo(expiredTime)
        );
    }

    @DisplayName("알림을 성공적으로 취소한다")
    @Test
    void cancel_alert() throws FirebaseMessagingException {
        // given
        LocalDateTime expiredTime = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
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
                () -> Assertions.assertThat(alertList).hasSize(1),
                () -> Assertions.assertThat(alertList.get(0).getTitle()).isEqualTo("title"),
                () -> Assertions.assertThat(alertList.get(0).getContent()).isEqualTo("content"),
                () -> Assertions.assertThat(alertList.get(0).getAlertTime()).isEqualTo(expiredTime)
        );
    }
}

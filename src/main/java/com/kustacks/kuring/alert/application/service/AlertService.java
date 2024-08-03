package com.kustacks.kuring.alert.application.service;

import com.kustacks.kuring.alert.application.port.in.AlertCommandUseCase;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.alert.application.port.out.AlertCommandPort;
import com.kustacks.kuring.alert.application.port.out.AlertQueryPort;
import com.kustacks.kuring.alert.application.port.out.MessageEventPort;
import com.kustacks.kuring.alert.application.port.out.dto.AlertDto;
import com.kustacks.kuring.alert.domain.Alert;
import com.kustacks.kuring.alert.domain.AlertStatus;
import com.kustacks.kuring.common.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class AlertService implements AlertCommandUseCase {

    private final ConcurrentMap<Long, ScheduledFuture<?>> taskList = new ConcurrentHashMap<>();
    private final AlertCommandPort alertCommandPort;
    private final AlertQueryPort alertQueryPort;
    private final MessageEventPort messageEventPort;
    private final TaskScheduler taskScheduler;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    @EventListener(ApplicationReadyEvent.class)
    public void initAlertSchedule() {
        List<AlertDto> entryAlerts = this.findAllPending();
        entryAlerts.forEach(this::addSchedule);
    }

    @Override
    public void addAlertSchedule(AlertCreateCommand command) {
        Alert newAlert = alertCommandPort.save(
                Alert.createIfValidAlertTime(
                        command.title(), command.content(), command.alertTime(), LocalDateTime.now()
                )
        );

        addSchedule(AlertDto.from(newAlert));
    }

    @Override
    public void cancelAlertSchedule(Long id) {
        ScheduledFuture<?> scheduled = taskList.remove(id);
        if (scheduled != null) {
            boolean cancelComplete = scheduled.cancel(false);

            if (cancelComplete) {
                alertQueryPort.findByIdAndStatusForUpdate(id, AlertStatus.PENDING)
                        .ifPresent(entryAlert -> cancle(id, entryAlert));
            }
        }
    }

    private static void cancle(Long id, Alert entryAlert) {
        entryAlert.changeCanceled();
        log.info("[EntryAlert 취소] entryAlertId: {}", id);
    }

    @Override
    public void sendAlert(Long id) {
        alertQueryPort.findByIdAndStatusForUpdate(id, AlertStatus.PENDING)
                .ifPresent(entryAlert -> send(id, entryAlert));
    }

    private void send(Long id, Alert entryAlert) {
        log.info("[EntryAlert 전송 시작] entryAlertId: {}", id);
        messageEventPort.sendMessageEvent(entryAlert.getTitle(), entryAlert.getContent());
        entryAlert.changeCompleted();
        taskList.remove(id);
    }

    private List<AlertDto> findAllPending() {
        return alertQueryPort.findAllByStatus(AlertStatus.PENDING)
                .stream()
                .map(AlertDto::from)
                .toList();
    }

    private void addSchedule(AlertDto alertDto) {
        Long alertId = alertDto.getId();
        Instant alertTime = toInstant(alertDto.getAlertTime());
        log.info("Alert 스케쥴링 추가. alertId: {}, alertTime: {}", alertId, alertTime);
        ScheduledFuture<?> scheduled = taskScheduler.schedule(() -> this.sendAlert(alertId), alertTime);
        taskList.put(alertId, scheduled);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(clock.getZone()).toInstant();
    }
}

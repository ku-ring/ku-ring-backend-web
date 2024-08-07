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
import org.springframework.transaction.support.TransactionTemplate;

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
    private final TransactionTemplate transactionTemplate;
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
                        command.title(), command.content(), command.alertTime(), LocalDateTime.now(clock)
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

    @Override
    public void sendAlert(Long id) {
        Alert findAlert = alertQueryPort.findByIdAndStatusForUpdate(id, AlertStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        send(id, findAlert.getTitle(), findAlert.getContent());

        findAlert.changeCompleted();
        taskList.remove(id);
    }

    private void cancle(Long id, Alert entryAlert) {
        entryAlert.changeCanceled();
        log.info("[EntryAlert 취소] entryAlertId: {}", id);
    }

    private void send(Long id, String title, String content) {
        log.info("[EntryAlert 전송 시작] entryAlertId: {}", id);
        messageEventPort.sendMessageEvent(title, content);
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

        ScheduledFuture<?> scheduled = taskScheduler.schedule(
                () -> transactionTemplate.execute(status -> {
                    sendAlert(alertId);
                    return null; // 트랜잭션 템플릿의 반환값, 필요에 따라 null 또는 다른 값을 반환
                }),
                alertTime
        );

        taskList.put(alertId, scheduled);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(clock.getZone()).toInstant();
    }
}

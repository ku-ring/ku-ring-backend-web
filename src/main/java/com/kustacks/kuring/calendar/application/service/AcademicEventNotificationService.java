package com.kustacks.kuring.calendar.application.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventNotificationUseCase;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.properties.ServerProperties;
import com.kustacks.kuring.message.application.port.out.FirebaseMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

import static com.kustacks.kuring.message.application.service.FirebaseSubscribeService.ACADEMIC_EVENT_TOPIC;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AcademicEventNotificationService implements AcademicEventNotificationUseCase {

    private static final String MESSAGE_TODAY_START = "[시작] %s";
    private static final String MESSAGE_TODAY_END = "[마감] %s";
    private static final String MESSAGE_TODAY = "[오늘] %s";
    private static final String MESSAGE_DEFAULT = "[일정 안내] %s";

    private final AcademicEventQueryPort academicEventQueryPort;
    private final FirebaseMessagingPort firebaseMessagingPort;
    private final ServerProperties serverProperties;

    @Override
    public void sendTodayAcademicEventNotifications() {
        LocalDate today = LocalDate.now();
        log.info("******** 학사일정 알림 발송 시작 ({}일자) ********", today);

        // 1. 오늘의 학사일정 조회
        List<AcademicEventReadModel> todayEvents = academicEventQueryPort.findTodayEvents(today);

        if (todayEvents.isEmpty()) {
            log.info("오늘 알림 발송할 학사일정이 없습니다.");
            return;
        }

        // 2. 각 일정별 알림 발송 (토픽 기반)
        todayEvents.forEach(event -> sendNotificationForEvent(event, today));

        log.info("******** 학사일정 알림 발송 완료 (총 {}개 일정) ********", todayEvents.size());
    }

    private void sendNotificationForEvent(AcademicEventReadModel event, LocalDate today) {
        String title = createTitle(event);
        String body = createBody(event, today);
        sendNotification(title, body);
    }

    private String createTitle(AcademicEventReadModel event) {
        return new StringBuilder()
                .append("[")
                .append(event.summary())
                .append("]")
                .toString();
    }

    private String createBody(AcademicEventReadModel event, LocalDate today) {
        if (event.isInProgressToday(today)) {
            // 시작일과 종료일이 모두 오늘인 경우
            return String.format(MESSAGE_TODAY, event.summary());
        } else if (event.isStartingToday(today)) {
            // 오늘 시작
            return String.format(MESSAGE_TODAY_START, event.summary());
        } else if (event.isEndingToday(today)) {
            // 오늘 종료
            return String.format(MESSAGE_TODAY_END, event.summary());
        } else {
            // 기본 메시지
            return String.format(MESSAGE_DEFAULT, event.summary());
        }
    }

    private void sendNotification(String title, String body) {
        Message message = makeMessage(title, body);
        try {
            firebaseMessagingPort.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("학사일정 FCM 전송에 실패했습니다.");
            throw new InternalLogicException(ErrorCode.FB_FAIL_SEND, e);
        } catch (Exception e) {
            log.error("학사일정을 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.");
            throw new InternalLogicException(ErrorCode.UNKNOWN_ERROR, e);
        }
    }

    private Message makeMessage(String title, String body) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setTopic(serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC))
                .build();
    }
}
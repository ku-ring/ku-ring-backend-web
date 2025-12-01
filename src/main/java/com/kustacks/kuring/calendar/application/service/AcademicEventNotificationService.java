package com.kustacks.kuring.calendar.application.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventNotificationUseCase;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.application.port.out.dto.AcademicEventReadModel;
import com.kustacks.kuring.common.annotation.UseCase;
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

    private static final String MESSAGE_DEFAULT = "오늘은 %s 일정이 있어요";

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
        int successCount = sendNotificationForEvents(todayEvents);

        log.info("******** 학사일정 알림 발송 완료 (총 {}개, 성공 {}개, 실패 {}개) ********", todayEvents.size(), successCount, todayEvents.size() - successCount);
    }

    private int sendNotificationForEvents(List<AcademicEventReadModel> todayEvents) {
        int successCount = 0;
        for (AcademicEventReadModel todayEvent : todayEvents) {
            boolean success = sendNotificationForEvent(todayEvent);
            successCount += success ? 1 : 0;
        }
        return successCount;
    }

    private boolean sendNotificationForEvent(AcademicEventReadModel event) {
        try {
            String title = createTitle(event);
            String body = createBody(event);
            sendNotificationMessage(title, body);

            log.info("학사일정(ID = {}) 알림 전송에 성공했습니다.", event.id());
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("학사일정(ID = {}) FCM 전송에 실패했습니다.", event.id());
            return false;
        } catch (Exception e) {
            log.error("학사일정(ID = {})을 FCM에 보내는 중 알 수 없는 오류가 발생했습니다.", event.id());
            return false;
        }
    }

    private String createTitle(AcademicEventReadModel event) {
        return new StringBuilder()
                .append("[")
                .append(event.summary())
                .append("]")
                .toString();
    }

    private String createBody(AcademicEventReadModel event) {
        return String.format(MESSAGE_DEFAULT, event.summary());
    }

    private void sendNotificationMessage(String title, String body) throws FirebaseMessagingException {
        Message message = makeMessage(title, body);
        firebaseMessagingPort.send(message);
    }

    private Message makeMessage(String title, String body) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setTopic(serverProperties.ifDevThenAddSuffix(ACADEMIC_EVENT_TOPIC))
                .putData("title", title)
                .putData("body", body)
                .putData("messageType", "academic")
                .build();
    }
}
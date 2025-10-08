package com.kustacks.kuring.worker.notification;

import com.kustacks.kuring.calendar.application.port.in.AcademicEventNotificationUseCase;
import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicEventNotificationScheduler {

    private final AcademicEventNotificationUseCase academicEventNotificationUseCase;
    private final FeatureFlags featureFlags;

    // 매일 아침 9시 학사일정 알림 전송
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendDailyAcademicEventNotifications() {
        if (featureFlags.isEnabled(KuringFeatures.NOTIFY_ACADEMIC_EVENT.getFeature())) {
            log.info("******** 일일 학사일정 알림 발송 시작 ********");
            try {
                academicEventNotificationUseCase.sendTodayAcademicEventNotifications();
                log.info("******** 일일 학사일정 알림 발송 완료 ********");
            } catch (Exception e) {
                log.error("학사일정 알림 발송 간 오류가 발생했습니다.", e);
            }
        }
    }
}
package com.kustacks.kuring.worker.notification;

import com.kustacks.kuring.club.application.port.in.ClubNotificationUseCase;
import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubNotificationScheduler {

    private final ClubNotificationUseCase clubNotificationUseCase;
    private final FeatureFlags featureFlags;

    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void sendClubDeadlineNotifications() {
        if (featureFlags.isEnabled(KuringFeatures.NOTIFY_CLUB_DEADLINE.getFeature())) {
            log.info("******** 동아리 마감 임박 알림 발송 시작 ********");
            try {
                clubNotificationUseCase.sendDeadlineNotifications();
                log.info("******** 동아리 마감 임박 알림 발송 완료 ********");
            } catch (Exception e) {
                log.error("동아리 마감 임박 알림 발송 중 오류가 발생했습니다.", e);
            }
        }
    }
}

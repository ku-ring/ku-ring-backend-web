package com.kustacks.kuring.worker.notification;

import com.kustacks.kuring.club.application.port.in.ClubNotificationUseCase;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("통합 테스트 : ClubNotificationScheduler")
class ClubNotificationSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private ClubNotificationScheduler scheduler;

    @SpyBean
    private ClubNotificationUseCase clubNotificationUseCase;

    @DisplayName("Feature Flag가 OFF이면 동아리 알림을 발송하지 않는다")
    @Test
    void should_not_send_notification_when_feature_flag_disabled() {
        featureFlagsSupport.setMapProperty(KuringFeatures.NOTIFY_CLUB_DEADLINE.getFeature().value(), false);

        assertThatCode(() -> scheduler.sendClubDeadlineNotifications())
                .doesNotThrowAnyException();

        verify(clubNotificationUseCase, never()).sendDeadlineNotifications();
    }

    @DisplayName("Feature Flag가 ON이면 동아리 알림 발송 유즈케이스를 호출한다")
    @Test
    void should_call_use_case_when_feature_flag_enabled() {
        featureFlagsSupport.setMapProperty(KuringFeatures.NOTIFY_CLUB_DEADLINE.getFeature().value(), true);

        assertThatCode(() -> scheduler.sendClubDeadlineNotifications())
                .doesNotThrowAnyException();

        verify(clubNotificationUseCase, times(1)).sendDeadlineNotifications();
    }
}

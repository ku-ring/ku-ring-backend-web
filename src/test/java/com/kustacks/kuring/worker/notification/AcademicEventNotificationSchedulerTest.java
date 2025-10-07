package com.kustacks.kuring.worker.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.calendar.adapter.out.persistence.AcademicEventRepository;
import com.kustacks.kuring.calendar.application.port.in.AcademicEventNotificationUseCase;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.AcademicEventCategory;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.message.adapter.out.firebase.FakeFirebaseAdapter;
import com.kustacks.kuring.support.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@DisplayName("통합 테스트 : AcademicEventNotificationScheduler")
class AcademicEventNotificationSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private AcademicEventNotificationScheduler scheduler;

    @Autowired
    private AcademicEventRepository academicEventRepository;

    @SpyBean
    private FakeFirebaseAdapter fakeFirebaseAdapter;

    @SpyBean
    private AcademicEventNotificationUseCase academicEventNotificationUseCase;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("Feature Flag가 OFF이면 알림을 발송하지 않는다")
    @Test
    void should_not_send_notification_when_feature_flag_disabled() {
        // given
        featureFlagsSupport.setMapProperty(KuringFeatures.NOTIFY_ACADEMIC_EVENT.getFeature().value(), false);

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then
        verify(academicEventNotificationUseCase, never()).sendTodayAcademicEventNotifications();
    }

    @DisplayName("오늘 일정 없음 = 알림 발송 안함")
    @Test
    void should_not_send_notification_when_no_events_today() throws FirebaseMessagingException {
        // given
        // 오늘이 아닌 다른 날의 일정만 생성
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        createTestAcademicEvent("내일 일정", tomorrow, tomorrow.plusHours(2));

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then
        verify(fakeFirebaseAdapter, never()).send(any());
    }

    @DisplayName("오늘 일정 있음 = 토픽으로 알림 발송")
    @Test
    void should_send_notification_to_topic_when_events_exist() throws FirebaseMessagingException {
        // given
        // 오늘 일정 생성
        LocalDateTime today = LocalDateTime.now();
        createTestAcademicEvent("오늘 일정", today, today.plusHours(2));

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then - 토픽으로 1번 발송(유저 1명)
        verify(fakeFirebaseAdapter, times(1)).send(any());
    }

    @DisplayName("오늘 시작하는 일정 알림 발송")
    @Test
    void should_send_notification_for_today_starting_event() throws FirebaseMessagingException {
        // given
        // 오늘 시작하는 일정 생성
        LocalDateTime today = LocalDateTime.now();
        createTestAcademicEvent("개강", today, today.plusDays(1));

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then - 토픽으로 1번 발송(유저 1명)
        verify(fakeFirebaseAdapter, times(1)).send(any());
    }

    @DisplayName("오늘 종료하는 일정 알림 발송")
    @Test
    void should_send_notification_for_today_ending_event() throws FirebaseMessagingException {
        // given
        // 오늘 종료하는 일정 생성
        LocalDateTime today = LocalDateTime.now();
        createTestAcademicEvent("종강", today.minusDays(1), today);

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then - 토픽으로 1번 발송
        verify(fakeFirebaseAdapter, times(1)).send(any());
    }

    @DisplayName("오늘 진행중인 일정 알림 발송")
    @Test
    void should_send_notification_for_today_in_progress_event() throws FirebaseMessagingException {
        // given
        // 오늘 진행중인 일정 생성 (시작일과 종료일이 모두 오늘)
        LocalDateTime today = LocalDateTime.now();
        createTestAcademicEvent("졸업식", today, today.plusHours(4));

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then - 토픽으로 1번 발송
        verify(fakeFirebaseAdapter, times(1)).send(any());
    }

    @DisplayName("여러 일정이 있는 경우 각 일정마다 알림 발송")
    @Test
    void should_send_notifications_for_multiple_events() throws FirebaseMessagingException {
        // given
        // 오늘 관련 일정 2개 생성
        LocalDateTime today = LocalDateTime.now();
        createTestAcademicEvent("개강", today, today.plusDays(1)); // 오늘 시작
        createTestAcademicEvent("종강", today.minusDays(1), today); // 오늘 종료

        // when & then
        assertThatCode(() -> scheduler.sendDailyAcademicEventNotifications())
                .doesNotThrowAnyException();

        // then
        // 2개 일정 = 2번 발송
        verify(fakeFirebaseAdapter, times(2)).send(any());
    }

    private AcademicEvent createTestAcademicEvent(String summary, LocalDateTime startTime, LocalDateTime endTime) {
        AcademicEvent event = AcademicEvent.builder()
                .eventUid(UUID.randomUUID().toString())
                .summary(summary)
                .description("테스트 이벤트: " + summary)
                .category(AcademicEventCategory.ETC)
                .transparent(Transparent.TRANSPARENT)
                .sequence(1)
                .notifyEnabled(true)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        event = academicEventRepository.save(event);

        entityManager.flush();
        entityManager.clear();

        return event;
    }

}
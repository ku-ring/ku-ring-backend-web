package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.notice.application.port.in.NoticeWeeklyRankingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeWeeklyRankingUpdater {

    private final NoticeWeeklyRankingUseCase noticeWeeklyRankingUseCase;
    private final FeatureFlags featureFlags;

    /*
    주간 조회수는 홈페이지 공지 업데이트가 켜져 있을 때만 집계한다.
    오후 10시 전체 공지 업데이트로 모든 공지의 조회수가 갱신된 뒤 집계하도록 일요일 오후 11시에 수행한다.
    */
    @Scheduled(cron = "0 0 23 * * SUN", zone = "Asia/Seoul")
    public void update() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_KUIS_HOMEPAGE_NOTICE.getFeature())) {
            log.info("******** 주간 인기 공지 집계 시작 ********");
            try {
                noticeWeeklyRankingUseCase.rankWeeklyTopNotices();
                log.info("******** 주간 인기 공지 집계 완료 ********");
            } catch (Exception e) {
                log.error("주간 인기 공지 집계 중 오류가 발생했습니다.", e);
            }
        }
    }

    /*
    배포 직후처럼 조회수 기준값이 없으면, 오후 10시 전체 공지 업데이트가 끝난 뒤의 조회수를 기준값으로 기록한다.
    */
    @Scheduled(cron = "0 0 23 * * MON-SAT", zone = "Asia/Seoul")
    public void initializeViewSnapshots() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_KUIS_HOMEPAGE_NOTICE.getFeature())) {
            try {
                noticeWeeklyRankingUseCase.initializeViewSnapshotsIfAbsent();
            } catch (Exception e) {
                log.error("공지 조회수 기준값 기록 중 오류가 발생했습니다.", e);
            }
        }
    }
}

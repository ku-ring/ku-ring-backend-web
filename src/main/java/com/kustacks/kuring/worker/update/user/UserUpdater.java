package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.user.application.port.out.RootUserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final UserCommandPort userCommandPort;
    private final RootUserCommandPort rootUserCommandPort;
    private final FeatureFlags featureFlags;

    @Scheduled(cron = "0 59 23 L * ?", zone = "Asia/Seoul") // 매달 마지막날 23:59에 실행
    public void questionCountReset() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_USER.getFeature())) {
            log.info("========== RAG 질문 토큰 초기화 시작 ==========");
            userCommandPort.resetAllUserQuestionCount();
            rootUserCommandPort.resetAllRootUserQuestionCount();
            log.info("========== RAG 질문 토큰 초기화 종료 ==========");
        }
    }
}

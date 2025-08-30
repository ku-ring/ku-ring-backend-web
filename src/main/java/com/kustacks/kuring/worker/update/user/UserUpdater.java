package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.config.featureflag.FeatureFlags;
import com.kustacks.kuring.config.featureflag.KuringFeatures;
import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.user.application.port.out.RootUserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final FirebaseWithUserUseCase firebaseService;
    private final UserCommandPort userCommandPort;
    private final RootUserCommandPort rootUserCommandPort;
    private final UserQueryPort userQueryPort;
    private final FeatureFlags featureFlags;

    @Scheduled(cron = "0 59 23 L * ?") // 매달 마지막날 23:59에 실행
    public void questionCountReset() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_USER.getFeature())) {
            log.info("========== RAG 질문 토큰 초기화 시작 ==========");
            userCommandPort.resetAllUserQuestionCount();
            rootUserCommandPort.resetAllRootUserQuestionCount();
            log.info("========== RAG 질문 토큰 초기화 종료 ==========");
        }
    }

    // 사용자 제거 로직에 일부 오류가 있는것 같다, 정상 사용자가 제거 되었다.
    //@Transactional
    //@Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    @Deprecated(since = "2.7.3", forRemoval = true)
    public void update() {
        if (featureFlags.isEnabled(KuringFeatures.UPDATE_USER.getFeature())) {
            log.info("========== 토큰 유효성 필터링 시작 ==========");

            Long totalUserCount = userQueryPort.countUser();
            log.debug("총 유저 수 = {}", totalUserCount);

            List<User> allInvalidUsers = new LinkedList<>();

            int totalPage = calculateTotalPage(totalUserCount);
            for (int page = 0; page < totalPage; page++) {
                List<User> invalidUsers = checkValidUserByPage(page);
                allInvalidUsers.addAll(invalidUsers);
            }

            userCommandPort.deleteAll(allInvalidUsers);

            log.info("========== 토큰 유효성 필터링 종료 ==========");
        }
    }

    private List<User> checkValidUserByPage(int page) {
        return userQueryPort.findByPageRequest(PageRequest.of(page, 500))
                .stream()
                .filter(user -> !isValidUser(user))
                .toList();
    }

    private boolean isValidUser(User user) {
        try {
//            firebaseService.validationToken(user.getToken());
            return true;
        } catch (FirebaseInvalidTokenException e) {
            return false;
        }
    }

    private static int calculateTotalPage(Long totalUserCount) {
        return (int) Math.ceil(totalUserCount / 500.0);
    }
}

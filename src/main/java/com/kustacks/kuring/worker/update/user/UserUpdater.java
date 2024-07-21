package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.message.application.port.in.FirebaseWithUserUseCase;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.user.application.port.out.UserCommandPort;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final FirebaseWithUserUseCase firebaseService;
    private final UserCommandPort userCommandPort;
    private final UserQueryPort userQueryPort;

    @Scheduled(cron = "0 59 23 L * ?") // 매달 마지막날 23:59에 실행
    public void questionCountReset() {
        log.info("========== RAG 질문 토큰 초기화 시작 ==========");
        userCommandPort.resetAllUserQuestionCount();
        log.info("========== RAG 질문 토큰 초기화 종료 ==========");
    }

    @Transactional
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void update() {
        log.info("========== 토큰 유효성 필터링 시작 ==========");

        Long totalUserCount = userQueryPort.countUser();
        log.debug("총 유저 수 = {}", totalUserCount);

        List<User> allInvalidUsers = new LinkedList<>();

        int totalPage = calculateTotalPage(totalUserCount);
        for(int page = 0; page < totalPage; page++) {
            List<User> invalidUsers = checkValidUserByPage(page);
            allInvalidUsers.addAll(invalidUsers);
        }

        userCommandPort.deleteAll(allInvalidUsers);

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }

    private List<User> checkValidUserByPage(int page) {
        return userQueryPort.findByPageRequest(PageRequest.of(page, 500))
                .stream()
                .filter(user -> !isValidUser(user))
                .toList();
    }

    private boolean isValidUser(User user) {
        try {
            firebaseService.validationToken(user.getToken());
            return true;
        } catch (FirebaseInvalidTokenException e) {
            return false;
        }
    }

    private static int calculateTotalPage(Long totalUserCount) {
        return (int) Math.ceil(totalUserCount / 500.0);
    }
}

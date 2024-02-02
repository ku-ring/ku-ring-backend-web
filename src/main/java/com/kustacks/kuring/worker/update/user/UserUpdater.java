package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.message.application.service.FirebaseService;
import com.kustacks.kuring.message.application.service.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.application.port.out.UserEventPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final FirebaseService firebaseService;
    private final UserPersistenceAdapter userPersistenceAdapter;
    private final UserEventPort userEventPort;

    @Transactional
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void update() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userPersistenceAdapter.findAll();

        for (User user : users) {
            String token = user.getToken();
            try {
                userEventPort.validationTokenEvent(token);
            } catch (FirebaseInvalidTokenException e) {
                userPersistenceAdapter.delete(user);
                log.info("삭제한 토큰 = {}", user.getToken());
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

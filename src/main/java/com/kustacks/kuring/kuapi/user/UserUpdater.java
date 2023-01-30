package com.kustacks.kuring.kuapi.user;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.domain.UserCategoryRepository;
import com.kustacks.kuring.kuapi.Updater;
import com.kustacks.kuring.common.firebase.FirebaseService;
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
public class UserUpdater implements Updater {

    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    @Transactional
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void update() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            String token = user.getToken();
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException e) {
                userCategoryRepository.deleteAllByUser(user);
                userRepository.deleteByToken(token);
                log.info("삭제한 토큰 = {}", token);
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

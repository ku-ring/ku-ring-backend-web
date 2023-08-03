package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.message.firebase.exception.FirebaseInvalidTokenException;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void update() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            String token = user.getToken();
            try {
                firebaseService.validationToken(token);
            } catch (FirebaseInvalidTokenException e) {
                userRepository.delete(user);
                log.info("삭제한 토큰 = {}", user.getToken());
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

package com.kustacks.kuring.kuapi;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.domain.user.User;
import com.kustacks.kuring.domain.user.UserRepository;
import com.kustacks.kuring.domain.user_category.UserCategoryRepository;
import com.kustacks.kuring.service.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class KuApiWatcher {

    private final FirebaseService firebaseService;

    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

//    private final int STAFF_UPDATE_RETRY_PERIOD = 1000 * 10;

    public KuApiWatcher(
            FirebaseService firebaseService,

            UserRepository userRepository,
            UserCategoryRepository userCategoryRepository
    ) {

        this.firebaseService = firebaseService;

        this.userRepository = userRepository;
        this.userCategoryRepository = userCategoryRepository;
    }



    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void verifyFCMTokens() {

        log.info("========== 토큰 유효성 필터링 시작 ==========");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            String token = user.getToken();
            try {
                firebaseService.verifyToken(token);
            } catch(FirebaseMessagingException e) {
                userCategoryRepository.deleteAll(user.getUserCategories());
                userRepository.deleteByToken(token);
                log.info("삭제한 토큰 = {}", token);
            }
        }

        log.info("========== 토큰 유효성 필터링 종료 ==========");
    }
}

package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdaterTest extends IntegrationTestSupport {

    @Autowired
    EntityManager em;

    @Autowired
    UserPersistenceAdapter userPersistenceAdapter;

    @Autowired
    UserUpdater userUpdater;

    @Transactional // em.flush(), clear()를 위해 설정
    @DisplayName("사용자 질문 카운트가 감소해도 매월 초에 초기값으로 다시 설정된다")
    @Test
    void questionCountReset() {
        // given
        User savedUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        savedUser.decreaseQuestionCount(); // 2 -> 1
        savedUser.decreaseQuestionCount(); // 1 -> 0
        em.flush();
        em.clear();

        // when
        userUpdater.questionCountReset();

        // then
        User findUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        int leftCount = findUser.decreaseQuestionCount();
        assertThat(leftCount).isEqualTo(User.FCM_USER_MONTHLY_QUESTION_COUNT - 1);
    }

    @Transactional
    @DisplayName("루트 사용자 질문 카운트가 감소해도 매월 초에 초기값으로 다시 설정된다")
    @Test
    void rootUserQuestionCountReset() {
        // given
        RootUser rootUser = new RootUser(USER_EMAIL, USER_PASSWORD, "쿠링이");
        RootUser savedRootUser = userPersistenceAdapter.saveRootUser(rootUser);
        savedRootUser.updateQuestionCount(0);
        em.flush();
        em.clear();

        // when
        userUpdater.questionCountReset(); // 질문 카운트 리셋 메서드 호출

        // then
        RootUser findRootUser = userPersistenceAdapter.findRootUserByEmail(USER_EMAIL).get();
        assertThat(findRootUser.getQuestionCount()).isEqualTo(RootUser.ROOT_USER_MONTHLY_QUESTION_COUNT);
    }
}

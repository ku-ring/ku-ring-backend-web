package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
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
        savedUser.decreaseQuestionCount(); // 3 -> 2
        savedUser.decreaseQuestionCount(); // 2 -> 1
        savedUser.decreaseQuestionCount(); // 1 -> 0
        em.flush();
        em.clear();

        // when
        userUpdater.questionCountReset();

        // then
        User findUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        int leftCount = findUser.decreaseQuestionCount();
        assertThat(leftCount).isEqualTo(User.MONTHLY_QUESTION_COUNT - 1);
    }
}

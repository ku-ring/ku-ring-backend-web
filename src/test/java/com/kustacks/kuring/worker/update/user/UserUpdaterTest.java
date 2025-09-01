package com.kustacks.kuring.worker.update.user;

import com.kustacks.kuring.common.featureflag.FeatureFlags;
import com.kustacks.kuring.common.featureflag.KuringFeatures;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.domain.RootUser;
import com.kustacks.kuring.user.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UserUpdaterTest extends IntegrationTestSupport {

    @Autowired
    EntityManager em;

    @Autowired
    UserPersistenceAdapter userPersistenceAdapter;

    @Autowired
    UserUpdater userUpdater;

    @MockBean
    private FeatureFlags featureFlags;

    @Transactional // em.flush(), clear()를 위해 설정
    @DisplayName("사용자 질문 카운트가 감소해도 매월 초에 초기값으로 다시 설정된다")
    @Test
    void questionCountReset() {
        // given
        when(featureFlags.isEnabled(any())).thenReturn(true);

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
        when(featureFlags.isEnabled(any())).thenReturn(true);

        RootUser savedRootUser = userPersistenceAdapter.findRootUserByEmail(USER_EMAIL).get();
        savedRootUser.updateQuestionCount(0);
        em.flush();
        em.clear();

        // when
        userUpdater.questionCountReset(); // 질문 카운트 리셋 메서드 호출

        // then
        RootUser findRootUser = userPersistenceAdapter.findRootUserByEmail(USER_EMAIL).get();
        assertThat(findRootUser.getQuestionCount()).isEqualTo(RootUser.ROOT_USER_MONTHLY_QUESTION_COUNT);
    }

    @Transactional
    @DisplayName("피쳐플레그가 false면 루트 사용자 질문 카운트 리셋이 실행되지 않는다")
    @Test
    void rootUserQuestionCountCantReset() {
        // given
        when(featureFlags.isEnabled(eq(KuringFeatures.UPDATE_USER.getFeature()))).thenReturn(false);
        RootUser savedRootUser = userPersistenceAdapter.findRootUserByEmail(USER_EMAIL).get();
        savedRootUser.updateQuestionCount(0);
        em.flush();
        em.clear();

        // when
        userUpdater.questionCountReset(); // 질문 카운트 리셋 메서드 호출

        // then
        RootUser findRootUser = userPersistenceAdapter.findRootUserByEmail(USER_EMAIL).get();
        assertThat(findRootUser.getQuestionCount()).isEqualTo(0);
    }
}

package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@DisplayName("리포지토리 : User")
class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    EntityManager em;

    @Autowired
    UserPersistenceAdapter userPersistenceAdapter;

    @DisplayName("사용자가 작성한 피드백을 페이징 처리하여 가져올 수 있다")
    @Test
    void findAllFeedbackByPageRequest() {
        // given
        User user = new User("user_token");
        user.addFeedback("content1");
        user.addFeedback("content2");
        user.addFeedback("content3");

        User savedUser = userPersistenceAdapter.save(user);
        Long userId = savedUser.getId();

        // when
        List<FeedbackDto> feedbackDtos = userPersistenceAdapter.findAllFeedbackByPageRequest(PageRequest.of(0, 3))
                .stream().toList();

        // then
        assertThat(feedbackDtos).hasSize(3)
                .extracting("contents", "userId")
                .containsExactlyInAnyOrder(
                        tuple("content1", userId),
                        tuple("content2", userId),
                        tuple("content3", userId)
                );
    }

    @DisplayName("사용자를 삭제하여도 피드벡은 남아있어야 한다")
    @Test
    void deleteALl() {
        // given
        User savedUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();

        // when
        userPersistenceAdapter.deleteAll(List.of(savedUser));

        // then
        Page<FeedbackDto> feedbackDtos = userPersistenceAdapter.findAllFeedbackByPageRequest(PageRequest.of(0, 10));
        assertThat(feedbackDtos).hasSize(5);
    }

    @Transactional
    @DisplayName("질문 카운트 감소가 성공적으로 영속화 되고, 0이 되면 IllegalStateException을 발생시킨다")
    @Test
    void decreaseQuestionCount() {
        // given
        User savedUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();
        savedUser.decreaseQuestionCount(); // 2 -> 1
        savedUser.decreaseQuestionCount(); // 1 -> 0
        em.flush();
        em.clear();
        savedUser = userPersistenceAdapter.findByToken(USER_FCM_TOKEN).get();

        // when
        ThrowableAssert.ThrowingCallable actual = savedUser::decreaseQuestionCount;

        // then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }
}

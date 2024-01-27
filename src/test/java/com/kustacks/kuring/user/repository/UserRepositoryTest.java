package com.kustacks.kuring.user.repository;

import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

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
        List<FeedbackDto> feedbackDtos = userPersistenceAdapter.findAllFeedbackByPageRequest(PageRequest.of(0, 3));

        // then
        assertThat(feedbackDtos).hasSize(3)
                .extracting("contents", "userId")
                .containsExactlyInAnyOrder(
                        tuple("content1", userId),
                        tuple("content2", userId),
                        tuple("content3", userId)
                );
    }
}

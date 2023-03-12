package com.kustacks.kuring.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserFeedbackTest {

    @DisplayName("피드백을 추가할 수 있다")
    @Test
    public void add_feedback() {
        // given
        User user = new User("token");

        // when
        user.addFeedback("피드백1");
        user.addFeedback("피드백2");

        // then
        assertThat(user.getAllFeedback().size()).isEqualTo(2);
    }

    @DisplayName("피드백을 모두 지울 수 있다")
    @Test
    public void clear_all_feedback() {
        // given
        User user = new User("token");
        user.addFeedback("피드백1");
        user.addFeedback("피드백2");

        // when
        user.clearFeedbacks();

        // then
        assertThat(user.getAllFeedback().size()).isEqualTo(0);
    }
}

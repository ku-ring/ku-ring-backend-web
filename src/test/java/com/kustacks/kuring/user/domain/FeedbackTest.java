package com.kustacks.kuring.user.domain;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("도메인 : Feedback")
public class FeedbackTest {

    @DisplayName("피드백을 생성할 수 있다")
    @Test
    void create_feedback() {
        // given
        User user = new User("token");

        // when, then
        assertThatCode(() -> new Feedback("contents!", user))
                .doesNotThrowAnyException();
    }

    @DisplayName("피드백 동등성 확인")
    @Test
    void feedback_equals() {
        // given
        User user = new User("token");
        Feedback feedback1 = createFeedback(1, "contents1", user);
        Feedback feedback2 = createFeedback(1, "contents2", user);

        // when, then
        assertThat(feedback1).isEqualTo(feedback2);
    }

    @DisplayName("피드백을 추가할 수 있다")
    @Test
    void add_feedback() {
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
    void clear_all_feedback() {
        // given
        User user = new User("token");
        user.addFeedback("피드백1");
        user.addFeedback("피드백2");

        // when
        user.clearFeedbacks();

        // then
        assertThat(user.getAllFeedback().size()).isEqualTo(0);
    }

    @DisplayName("256자 초과의 피드백은 예외를 발생시킨다")
    @ParameterizedTest
    @MethodSource("invalidLengthContentsInputProvider")
    void invalid_length_feedback(String content, String errorMessage) {
        // given
        User user = new User("token");

        // when
        ThrowingCallable actual = () -> user.addFeedback(content);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    private static Stream<Arguments> invalidLengthContentsInputProvider() {
        return Stream.of(
                Arguments.of("https://www.google.com/search?q=%EC%95%88%EB%8dsfajaslkfjasdkfjkldsafjlsadkjf" +
                        "lksajfldkadsfajaslkfjasdkfjkldsafjlsadkjflksajfldkajlafkj;lkdjalkfjads;jfalksdfjlasjf;" +
                        "ljlfsaddsfajaslkfjasdkfjkldsafjlsadkjflksajfldkajlafkj;lkdjalkfjads;jfalksdfjlasjf;ljlf" +
                        "sadjlafkj;lkdjalkfjads", "본문 내용은 256자 이하여야 합니다"),
                Arguments.of("", "본문은 공백일 수 없습니다")
        );
    }

    private Feedback createFeedback(long id, String content, User user) {
        Feedback feedback = new Feedback(content, user);
        ReflectionTestUtils.setField(feedback, "id", id);
        return feedback;
    }
}

package com.kustacks.kuring.user.domain;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("도메인 : Feedback")
public class FeedbackTest {

    @DisplayName("피드백을 생성할 수 있다")
    @Test
    public void create_feedback() {
        // given
        User user = new User("token");

        // when, then
        assertThatCode(() -> new Feedback("contents!", user))
                .doesNotThrowAnyException();
    }

    @DisplayName("피드백 동등성 확인")
    @Test
    public void feedback_equals() {
        // given
        User user = new User("token");
        Feedback feedback1 = new Feedback("contents1", user);
        Feedback feedback2 = new Feedback("contents2", user);

        // when, then
        assertThat(feedback1).isEqualTo(feedback2);
    }

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

    @DisplayName("256자 초과의 피드백은 예외를 발생시킨다")
    @ParameterizedTest
    @MethodSource("invalidLengthContentsInputProvider")
    public void invalid_length_feedback(String content, String errorMessage) {
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
}

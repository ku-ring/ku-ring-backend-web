package com.kustacks.kuring.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UserTest {

    @DisplayName("User 생성 테스트")
    @Test
    public void creat_user() {
        assertThatCode(() -> new User("token"))
                .doesNotThrowAnyException();
    }

    @DisplayName("User의 ID가 같은 경우 equals를 통해 동일한 객체로 판단하는지 확인한다.")
    @Test
    void compare_equal_test() {
        User userOne = createUser(1L, "token_one");
        User userTwo = createUser(1L, "token_two");
        assertThat(userOne).isEqualTo(userTwo);
    }

    @DisplayName("User Id가 다른 경우 equals를 통해 다른 객체로 판단한다.")
    @Test
    void compare_not_equal_test() {
        User userOne = createUser(1L, "token_one");
        User userTwo = createUser(2L, "token_one");
        assertThat(userOne).isNotEqualTo(userTwo);
    }

    private User createUser(Long id, String token) {
        User user = new User(token);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}

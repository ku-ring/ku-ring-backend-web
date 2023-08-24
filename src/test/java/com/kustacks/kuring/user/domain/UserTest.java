package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UserTest {

    @DisplayName("User 생성 테스트")
    @Test
    void creat_user() {
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

    @DisplayName("사용자가 구독한 카테고리 이름을 저장한다")
    @Test
    void add_category() {
        // given
        User user = createUser(1L, "token_one");

        // when
        user.subscribeCategory(CategoryName.NORMAL);
        user.subscribeCategory(CategoryName.BACHELOR);

        // then
        assertThat(user.getSubscribedCategoryList()).contains(CategoryName.NORMAL, CategoryName.BACHELOR);
    }

    @DisplayName("신규로 저장될 카테고리 이름 목록을 반환한다")
    @Test
    void filtering_new_category_name() {
        // given
        User user = createUser(1L, "token_one");
        user.subscribeCategory(CategoryName.NORMAL);
        user.subscribeCategory(CategoryName.BACHELOR);

        // when
        List<CategoryName> results = user.filteringNewCategoryName(List.of(CategoryName.NORMAL, CategoryName.EMPLOYMENT, CategoryName.NATIONAL));

        // then
        assertThat(results).contains(CategoryName.EMPLOYMENT, CategoryName.NATIONAL);
    }

    @DisplayName("이전에 저장된 카테고리 중 이번에 삭제될 목록을 반환")
    @Test
    void filtering_old_category_name() {
        // given
        User user = createUser(1L, "token_one");
        user.subscribeCategory(CategoryName.NORMAL);
        user.subscribeCategory(CategoryName.BACHELOR);

        // when
        List<CategoryName> results = user.filteringOldCategoryName(List.of(CategoryName.NORMAL, CategoryName.EMPLOYMENT, CategoryName.NATIONAL));

        // then
        assertThat(results).contains(CategoryName.BACHELOR);
    }

    @DisplayName("사용자가 구독한 학과이름을 저장한다")
    @Test
    void add_department() {
        // given
        User user = createUser(1L, "token_one");

        // when
        user.subscribeDepartment(DepartmentName.KOREAN);
        user.subscribeDepartment(DepartmentName.ENGLISH);

        // then
        assertThat(user.getSubscribedDepartmentList()).contains(DepartmentName.KOREAN, DepartmentName.ENGLISH);
    }

    @DisplayName("신규로 저장될 학과 이름 목록을 반환한다")
    @Test
    void filtering_new_department_name() {
        // given
        User user = createUser(1L, "token_one");
        user.subscribeDepartment(DepartmentName.KOREAN);
        user.subscribeDepartment(DepartmentName.ENGLISH);

        // when
        List<DepartmentName> results = user.filteringNewDepartmentName(List.of(DepartmentName.KOREAN, DepartmentName.COMPUTER, DepartmentName.MATH));

        // then
        assertThat(results).contains(DepartmentName.COMPUTER, DepartmentName.MATH);
    }

    @DisplayName("이전에 저장된 학과중 이번에 삭제될 목록을 반환")
    @Test
    void filtering_old_department_name() {
        // given
        User user = createUser(1L, "token_one");
        user.subscribeDepartment(DepartmentName.KOREAN);
        user.subscribeDepartment(DepartmentName.ENGLISH);

        // when
        List<DepartmentName> results = user.filteringOldDepartmentName(List.of(DepartmentName.KOREAN, DepartmentName.COMPUTER, DepartmentName.MATH));

        // then
        assertThat(results).contains(DepartmentName.ENGLISH);
    }

    private User createUser(Long id, String token) {
        User user = new User(token);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}

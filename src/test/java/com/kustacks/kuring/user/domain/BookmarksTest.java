package com.kustacks.kuring.user.domain;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("도메인 : Bookmarks")
class BookmarksTest {

    @DisplayName("사용자는 원하는 공지를 북마크 할 수 있다")
    @Test
    void add() {
        // given
        User user = new User("token");
        String noticeId = "1234";

        // when, then
        assertThatCode(() -> user.addBookmark(noticeId))
                .doesNotThrowAnyException();
    }

    @DisplayName("사용자는 원하는 공지를 북마크 할 수 있다")
    @Test
    void lookup_all_bookmark_ids() {
        // given
        User user = new User("token");
        user.addBookmark("1");
        user.addBookmark("2");
        user.addBookmark("3");
        user.addBookmark("4");

        // when
        List<String> ids = user.lookupAllBookmarkIds();

        // then
        assertThat(ids).hasSize(4)
                .containsOnly("1", "2", "3", "4");
    }

    @DisplayName("사용자는 공지를 10개까지 북마크 할 수 있다")
    @Test
    void user_bookmark_limit() {
        // given
        User user = new User("token");
        for(int i = 1; i <= 10; i++) user.addBookmark(String.valueOf(i));

        // when
        ThrowableAssert.ThrowingCallable actual = () -> user.addBookmark(String.valueOf(11));

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("북마크가 저장 가능한 사이즈를 초과하였습니다.");
    }
}

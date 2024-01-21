package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.acceptance.AcceptanceTest;
import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.notice.domain.*;
import com.kustacks.kuring.user.common.dto.BookmarkDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


class UserRepositoryTest extends AcceptanceTest {

    @Autowired
    private UserQueryRepositoryImpl userQueryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeJdbcRepository noticeJdbcRepository;

    @DisplayName("사용자가 작성한 피드백을 페이징 처리하여 가져올 수 있다")
    @Test
    public void findAllFeedbackByPageRequest() {
        // given
        User user = new User("user_token");
        user.addFeedback("content1");
        user.addFeedback("content2");
        user.addFeedback("content3");

        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        // when
        List<FeedbackDto> feedbackDtos = userQueryRepository.findAllFeedbackByPageRequest(PageRequest.of(0, 3));

        // then
        assertThat(feedbackDtos).hasSize(3)
                .extracting("contents", "userId")
                .containsExactlyInAnyOrder(
                        tuple("content1", userId),
                        tuple("content2", userId),
                        tuple("content3", userId)
                );
    }

    @DisplayName("사용자가 북마크해둔 공지의 ID로 해당 공지들을 찾아올 수 있다")
    @Test
    public void lookupAllNoticeByIds() {
        // given
        Notice notice1 = new Notice("1", "2024-01-19", "updatedDate",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20", "updatedDate",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        noticeJdbcRepository.saveAllCategoryNotices(List.of(notice1, notice2));

        DepartmentNotice departmentNotice1 = new DepartmentNotice("3", "2024-01-22", "updatedDate",
                "departmentNotice1", CategoryName.DEPARTMENT, false, "https://www.example.com", DepartmentName.ADMINISTRATION);
        DepartmentNotice departmentNotice2 = new DepartmentNotice("4", "2024-01-24", "updatedDate",
                "departmentNotice2", CategoryName.DEPARTMENT, false, "https://www.example.com", DepartmentName.ADMINISTRATION);
        noticeJdbcRepository.saveAllDepartmentNotices(List.of(departmentNotice1, departmentNotice2));

        User user = new User("user_token");
        user.addBookmark(notice1.getArticleId());
        user.addBookmark(notice2.getArticleId());
        user.addBookmark(departmentNotice1.getArticleId());
        user.addBookmark(departmentNotice2.getArticleId());

        User savedUser = userRepository.save(user);
        List<String> ids = savedUser.lookupAllBookmarkIds();

        // when
        List<BookmarkDto> bookmarks = userQueryRepository.findAllBookmarks(ids);

        // then
        assertThat(bookmarks).hasSize(4)
                .extracting("articleId", "postedDate", "subject")
                .containsExactly(
                        tuple("4", "2024-01-24", "departmentNotice2"),
                        tuple("3", "2024-01-22", "departmentNotice1"),
                        tuple("2", "2024-01-20", "notice2"),
                        tuple("1", "2024-01-19", "notice1")
                );
    }
}

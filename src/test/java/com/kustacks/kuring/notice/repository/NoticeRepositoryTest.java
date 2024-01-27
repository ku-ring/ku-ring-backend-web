package com.kustacks.kuring.notice.repository;

import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import com.kustacks.kuring.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class NoticeRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private NoticeQueryPort noticeQueryPort;

    @Autowired
    private NoticeCommandPort noticeCommandPort;

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @DisplayName("사용자가 북마크해둔 공지의 ID로 해당 공지들을 찾아올 수 있다")
    @Test
    void lookupAllNoticeByIds() {
        // given
        Notice notice1 = new Notice("1", "2024-01-19", "updatedDate",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20", "updatedDate",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2));

        DepartmentNotice departmentNotice1 = new DepartmentNotice("3", "2024-01-22", "updatedDate",
                "departmentNotice1", CategoryName.DEPARTMENT, false, "https://www.example.com", DepartmentName.ADMINISTRATION);
        DepartmentNotice departmentNotice2 = new DepartmentNotice("4", "2024-01-24", "updatedDate",
                "departmentNotice2", CategoryName.DEPARTMENT, false, "https://www.example.com", DepartmentName.ADMINISTRATION);
        noticeCommandPort.saveAllDepartmentNotices(List.of(departmentNotice1, departmentNotice2));

        User user = new User("user_token");
        user.addBookmark(notice1.getArticleId());
        user.addBookmark(notice2.getArticleId());
        user.addBookmark(departmentNotice1.getArticleId());
        user.addBookmark(departmentNotice2.getArticleId());

        User savedUser = userPersistenceAdapter.save(user);
        List<String> ids = savedUser.lookupAllBookmarkIds();

        // when
        List<BookmarkDto> bookmarks = noticeQueryPort.findAllByBookmarkIds(ids);

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

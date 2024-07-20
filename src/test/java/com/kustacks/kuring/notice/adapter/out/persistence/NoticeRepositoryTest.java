package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("리포지토리 : Notice")
class NoticeRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private NoticeQueryPort noticeQueryPort;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeCommandPort noticeCommandPort;

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @DisplayName("jdbc를 사용한 bulk insert 테스트")
    @Test
    void jdbcBulkInsert() {
        // given
        noticeRepository.deleteAll();
        List<Notice> notices = creatNotices(70);

        // when
        noticeCommandPort.saveAllCategoryNotices(notices);

        // then
        List<Notice> findNotices = noticeRepository.findAll();
        assertThat(findNotices).hasSize(70);
    }

    @DisplayName("사용자가 북마크해둔 공지의 ID로 해당 공지들을 찾아올 수 있다")
    @Test
    void lookupAllNoticeByIds() {
        // given
        Notice notice1 = new Notice("1", "2024-01-19 17:27:05", "2023-04-03 17:27:05",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20 17:27:05", "2023-04-03 17:27:05",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2));

        DepartmentNotice departmentNotice1 = new DepartmentNotice("3", "2024-01-22 17:27:05", "2023-04-03 17:27:05",
                "departmentNotice1", CategoryName.DEPARTMENT, false, "https://www.example.com", DepartmentName.ADMINISTRATION);
        DepartmentNotice departmentNotice2 = new DepartmentNotice("4", "2024-01-24 17:27:05", "2023-04-03 17:27:05",
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

    @DisplayName("공지 중요도를 변경할 수 있다")
    @Test
    void notice_change_important() {
        // given
        Notice notice1 = new Notice("1", "2024-01-19 17:27:05", "2023-04-03 17:27:05",
                "notice1", CategoryName.BACHELOR, true, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20 17:27:05", "2023-04-03 17:27:05",
                "notice2", CategoryName.BACHELOR, true, "https://www.example.com");
        Notice notice3 = new Notice("3", "2024-01-20 17:27:05", "2023-04-03 17:27:05",
                "notice3", CategoryName.BACHELOR, true, "https://www.example.com");

        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2, notice3));

        // when
        noticeCommandPort.changeNoticeImportantToFalseByArticleId(CategoryName.BACHELOR, List.of("1", "2"));

        // then
        List<String> normalArticleIds = noticeQueryPort.findNormalArticleIdsByCategoryName(CategoryName.BACHELOR);
        assertThat(normalArticleIds).containsExactly("1", "2");
    }

    @DisplayName("Embedding 된 공지의 상태를 성공적으로 영속화 한다")
    @Test
    void embeddingNotice() {
        // given
        noticeRepository.deleteAll();
        Notice notice1 = new Notice("1", "2024-03-19 17:27:05", "2023-04-03 17:27:05",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-19 17:27:06", "2023-04-03 17:27:05",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice3 = new Notice("3", "2024-01-19 17:27:05", "2023-04-03 17:27:05",
                "notice3", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice4 = new Notice("4", "2024-01-18 17:27:05", "2023-04-03 17:27:05",
                "notice4", CategoryName.BACHELOR, false, "https://www.example.com");

        notice1.embeddedSuccess();
        notice3.embeddedSuccess();

        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2, notice3, notice4));

        // when
        List<Notice> notices = noticeRepository.findAll();

        // then
        assertAll(
                () -> assertThat(notices).hasSize(4),
                () -> assertThat(notices).extracting("articleId")
                        .containsExactly("1", "2", "3", "4"),
                () -> assertThat(notices).extracting("embedded")
                        .containsExactly(true, false, true, false)
        );
    }

    @DisplayName("지정된 기간 동안 Embedding 되지 않은 공지를 찾아올 수 있다")
    @Test
    void findNotYetEmbeddingNotice() {
        // given
        Notice notice1 = new Notice("1", "2024-03-19 17:27:07", "2023-04-03 17:27:05",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20 00:00:00", "2023-04-03 17:27:05",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice3 = new Notice("3", "2024-01-19 17:27:05", "2023-04-03 17:27:05",
                "notice3", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice4 = new Notice("4", "2024-01-18 17:27:05", "2023-04-03 17:27:05",
                "notice4", CategoryName.BACHELOR, false, "https://www.example.com");

        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2, notice3, notice4));

        // when
        List<NoticeDto> results = noticeQueryPort.findNotYetEmbeddingNotice(
                CategoryName.BACHELOR,
                LocalDateTime.of(2024, 3, 19, 17, 27, 5).minusMonths(2)
        );

        // then
        assertAll(
                () -> assertThat(results).hasSize(2),
                () -> assertThat(results).extracting("articleId")
                        .contains("1", "2")
        );
    }

    @DisplayName("Embedding 된 공지의 상태를 변경할 수 있다")
    @Test
    void updateNoticeEmbeddingStatus() {
        // given
        Notice notice1 = new Notice("1", "2024-03-19 17:27:07", "2023-04-03 17:27:05",
                "notice1", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice2 = new Notice("2", "2024-01-20 00:00:00", "2023-04-03 17:27:05",
                "notice2", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice3 = new Notice("3", "2024-01-21 17:27:05", "2023-04-03 17:27:05",
                "notice3", CategoryName.BACHELOR, false, "https://www.example.com");
        Notice notice4 = new Notice("4", "2024-01-22 17:27:05", "2023-04-03 17:27:05",
                "notice4", CategoryName.BACHELOR, false, "https://www.example.com");

        noticeCommandPort.saveAllCategoryNotices(List.of(notice1, notice2, notice3, notice4));

        // when
        noticeCommandPort.updateNoticeEmbeddingStatus(CategoryName.BACHELOR, List.of("1", "4"));

        // then
        List<NoticeDto> results = noticeQueryPort.findNotYetEmbeddingNotice(
                CategoryName.BACHELOR,
                LocalDateTime.of(2024, 3, 19, 17, 27, 5).minusMonths(2)
        );

        // then
        assertAll(
                () -> assertThat(results).hasSize(2),
                () -> assertThat(results).extracting("articleId")
                        .contains("2", "3")
        );
    }

    private List<Notice> creatNotices(int count) {
        List<Notice> notices = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            notices.add(new Notice(String.valueOf(i), "2024-01-19 17:27:05", "2023-04-03 17:27:05",
                    "notice" + i, CategoryName.BACHELOR, false, "https://www.example.com"));
        }
        return notices;
    }
}

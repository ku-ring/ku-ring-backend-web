package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.common.error.DomainLogicException;
import com.kustacks.kuring.category.domain.CategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("도메인 : Notice")
class NoticeTest {

    @DisplayName("공지 생성 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"https://www.example.com", "http://example.com:8080/path/to/resource",
            "https://library.konkuk.ac.kr/library-guide/bulletins/notice/7192", "http://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do?forum=notice&sort=6&id=5b50736&cat=0000300001",
    "http://mae.konkuk.ac.kr/noticeView.do?siteId=MAE&boardSeq=988&menuSeq=6823&categorySeq=0&curBoardDispType=LIST&curPage=12&pageNum=1&seq=179896"})
    void create_member(String url) {
        assertThatCode(() -> new Notice("artice_id", "postDate", "updatedDate", "subject", new Category(CategoryName.BACHELOR), false, url))
                .doesNotThrowAnyException();
    }

    @DisplayName("공지의 url 형식 검증 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"//www.example.com", "https:/www.example.com", "https://"})
    void member_invalid_email_id(String url) {
        assertThatThrownBy(() -> new Notice("artice_id", "postDate", "updatedDate", "subject", new Category(CategoryName.BACHELOR), false, url))
                .isInstanceOf(DomainLogicException.class);
    }

}

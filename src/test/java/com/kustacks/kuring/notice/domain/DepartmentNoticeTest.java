package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.error.DomainLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("도메인 : DepartmentNotice")
class DepartmentNoticeTest {

    @DisplayName("학과 공지 생성 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"https://www.example.com", "http://example.com:8080/path/to/resource",
            "https://library.konkuk.ac.kr/library-guide/bulletins/notice/7192", "http://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do?forum=notice&sort=6&id=5b50736&cat=0000300001",
            "http://mae.konkuk.ac.kr/noticeView.do?siteId=MAE&boardSeq=988&menuSeq=6823&categorySeq=0&curBoardDispType=LIST&curPage=12&pageNum=1&seq=179896"})
    void create_member(String url) {
        assertThatCode(() -> new DepartmentNotice("artice_id", "postDate", "updatedDate", "subject", CategoryName.DEPARTMENT, false, url, DepartmentName.BIOLOGICAL))
                .doesNotThrowAnyException();
    }

    @DisplayName("공지의 url 형식 검증 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"//www.example.com", "https:/www.example.com", "https://"})
    void member_invalid_email_id(String url) {
        assertThatThrownBy(() -> new DepartmentNotice("artice_id", "postDate", "updatedDate", "subject", CategoryName.DEPARTMENT, false, url, DepartmentName.BIOLOGICAL))
                .isInstanceOf(DomainLogicException.class);
    }

    @DisplayName("동등성 검증 테스트")
    @Test
    public void notice_equals_test() {
        DepartmentNotice notice1 = createDepartmentNotice(1L, "https://www.one.com");
        DepartmentNotice notice2 = createDepartmentNotice(1L, "https://www.two.com");
        Assertions.assertThat(notice1.equals(notice2)).isTrue();
    }

    private DepartmentNotice createDepartmentNotice(long id, String url) {
        DepartmentNotice departmentNotice = new DepartmentNotice("artice_id", "postDate", "updatedDate", "subject", CategoryName.DEPARTMENT, false, url, DepartmentName.BIOLOGICAL);
        ReflectionTestUtils.setField(departmentNotice, "id", id);
        return departmentNotice;
    }
}

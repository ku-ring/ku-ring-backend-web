package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.support.TestFileLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoticeTextParserTemplateTest {

    @DisplayName("공지의 텍스트를 파싱한다")
    @ParameterizedTest
    @MethodSource("noticeTextInputProvider")
    void NoticeTextParser(
            String path, String articleId, String title, List<String> textBody
    ) throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile(path));

        // when
        PageTextDto results = new KuisHomepageNoticeTextParser().parse(doc);

        // then
        assertAll(
                () -> assertThat(results.articleId()).isEqualTo(articleId),
                () -> assertThat(results.title()).isEqualTo(title),
                () -> assertThat(results.text()).contains(textBody)
        );
    }

    private static Stream<Arguments> noticeTextInputProvider() {
        return Stream.of(
                Arguments.of("src/test/resources/notice/bbs-article-2-2024.html",
                        "1129848", "2024학년도 2학기 다·부·연계·융합전공 이수 및 포기 신청 안내",
                        List.of("2024학년도 2학기 다·부·연계·융합전공 이수 신청 안내", "문의사항: 학사팀 02-450-3192")
                ),
                Arguments.of("src/test/resources/notice/bbs-article-2024.html",
                        "1117110", "2024년도 대학생 청소년교육지원 장학사업 멘토모집 안내",
                        List.of("1. 멘토 신청기간: 2024. 3. 18. (월) ~ 2023. 3. 25. (월)",
                                "2. 모집유형: 나눔지기(멘토) 발굴형",
                                "3. 신청방법: 기관 협의 완료 후 한국장학재단 사이트에서 신청",
                                "4. 멘토 선발 및 매칭기간: 2023. 3. 26. (화) ~ 2023. 3. 29. (금)",
                                "5. 멘토 모집인원: 15명",
                                "6. 멘토 활동기간: 2024. 4. ~ 2025. 2",
                                "7. 멘토 선발기준",
                                "8. 멘토활동 세부사항",
                                "9. 기타")
                )
        );
    }
}

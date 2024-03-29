package com.kustacks.kuring.worker.parser;

import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.parser.notice.*;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoticeHtmlParserTemplateTest {

    @DisplayName("신규 2024 홈페이지의 학생 공지를 분석한다")
    @Test
    void KuisHomepageNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html"));
        String viewUrl = "https://www.konkuk.ac.kr/bbs/konkuk/238/{noticeId}/artclView.do";

        // when
        RowsDto rowsDto = new KuisHomepageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList(viewUrl);
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList(viewUrl);

        // then
        assertAll(
                () -> assertThat(important).hasSize(22),
                () -> assertThat(normal).hasSize(10),
                () -> assertThat(normal)
                        .extracting("articleId", "subject", "postedDate", "fullUrl", "important")
                        .containsExactlyInAnyOrder(
                                tuple("1115719", "2024년 학군사관 후보생(65기, 66기) 모집 안내", "2024.03.08", "https://www.konkuk.ac.kr/bbs/konkuk/238/1115719/artclView.do", false),
                                tuple("1116631", "2024 건대신문 제68기 수습기자 정기 공개채용", "2024.03.07", "https://www.konkuk.ac.kr/bbs/konkuk/238/1116631/artclView.do", false),
                                tuple("1116544", "일우헌 공인회계사 준비반 신입실원 모집", "2024.03.06", "https://www.konkuk.ac.kr/bbs/konkuk/238/1116544/artclView.do", false),
                                tuple("1116114", "1학기1차 외국어특별장학생 신청 안내", "2024.03.04", "https://www.konkuk.ac.kr/bbs/konkuk/238/1116114/artclView.do", false),
                                tuple("1115984", "로스쿨 준비반 모집 공고", "2024.02.29", "https://www.konkuk.ac.kr/bbs/konkuk/238/1115984/artclView.do", false),
                                tuple("1115956", "건국대 학생리포터 투데이건국 12기 모집", "2024.02.29", "https://www.konkuk.ac.kr/bbs/konkuk/238/1115956/artclView.do", false),
                                tuple("1115854", "[혁신사업] 대학혁신지원사업 서포터즈(4기) 모집", "2024.02.28", "https://www.konkuk.ac.kr/bbs/konkuk/238/1115854/artclView.do", false),
                                tuple("1115717", "일우헌 5급공채반 24년도 1학기 신입생 모집안내", "2024.02.27", "https://www.konkuk.ac.kr/bbs/konkuk/238/1115717/artclView.do", false),
                                tuple("5408", "건국대학교 홍보실 소속 공식 학생 크리에이터 '쿠(KU)리에이터' 10기 모집", "2024.02.20", "https://www.konkuk.ac.kr/bbs/konkuk/238/5408/artclView.do", false),
                                tuple("5407", "[정보운영팀] 2024학년도 1학기 교내근로장학생 모집 공고 (※근무가능 시간표 첨부파일 수정※)", "2024.02.20", "https://www.konkuk.ac.kr/bbs/konkuk/238/5407/artclView.do", false)
                        )
        );
    }

    @DisplayName("신규 개편된 학과의 홈페이지 공지를 분석한다")
    @Test
    void LatestPageNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/cse-notice-2024.html"));
        String viewUrl = "https://cse.konkuk.ac.kr/bbs/cse/775/{uniqueNoticeId}/artclView.do";

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList(viewUrl);
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList(viewUrl);

        // then
        assertAll(
                () -> assertThat(important).hasSize(15),
                () -> assertThat(important)
                        .extracting("articleId", "subject", "postedDate", "fullUrl", "important")
                        .containsExactlyInAnyOrder(
                                tuple("5737", "[졸업] 2024학년도 제135회 학위수여식 관련 졸업가능여부 조회 및 행사 안내", "2024.02.16", "https://cse.konkuk.ac.kr/bbs/cse/775/5737/artclView.do", true),
                                tuple("828684", "[졸업] 2024년 전기 학위복 대여 및 학위기 배부 장소 안내", "2024.02.05", "https://cse.konkuk.ac.kr/bbs/cse/775/828684/artclView.do", true),
                                tuple("828683", "[학사] 2024년도 2월 미졸업자 수강신청학점(미졸코드) 변경 안내", "2024.02.02", "https://cse.konkuk.ac.kr/bbs/cse/775/828683/artclView.do", true),
                                tuple("828681", "[학사] 2024학년도 건국대학교 학부 온라인 요람 발간 안내", "2024.01.31", "https://cse.konkuk.ac.kr/bbs/cse/775/828681/artclView.do", true),
                                tuple("828680", "[교직] 교직이수예정자의 교직소양 영역 최저이수기준 안내", "2024.01.31", "https://cse.konkuk.ac.kr/bbs/cse/775/828680/artclView.do", true),
                                tuple("828672", "[학사] 2024-1학기 수강바구니(수강신청) 일정 및 유의사항 안내", "2024.01.12", "https://cse.konkuk.ac.kr/bbs/cse/775/828672/artclView.do", true),
                                tuple("828670", "[학사] 2024학년도 1학기 조기졸업 신청 안내", "2024.01.08", "https://cse.konkuk.ac.kr/bbs/cse/775/828670/artclView.do", true),
                                tuple("828669", "[현장실습] 2024학년도 1학기 현장실습학기제 안내", "2024.01.08", "https://cse.konkuk.ac.kr/bbs/cse/775/828669/artclView.do", true),
                                tuple("828665", "[학사] 2024-1학기 학사구조개편 관련 소속변경 안내", "2023.12.07", "https://cse.konkuk.ac.kr/bbs/cse/775/828665/artclView.do", true),
                                tuple("828653", "[학사] 가사휴학 연한 제한 제도 시행 안내", "2023.11.03", "https://cse.konkuk.ac.kr/bbs/cse/775/828653/artclView.do", true),
                                tuple("828351", "[졸업] 컴퓨터공학부 학과별 졸업요건 정리", "2022.02.08", "https://cse.konkuk.ac.kr/bbs/cse/775/828351/artclView.do", true),
                                tuple("828243", "[졸업] 인턴십 의무이수제 졸업요건 안내", "2021.06.23", "https://cse.konkuk.ac.kr/bbs/cse/775/828243/artclView.do", true),
                                tuple("828163", "[졸업] 교과목명 신구대비표", "2021.01.25", "https://cse.konkuk.ac.kr/bbs/cse/775/828163/artclView.do", true),
                                tuple("828089", "[내규] 교환학생 전공학점인정 관련 내규 안내", "2020.07.28", "https://cse.konkuk.ac.kr/bbs/cse/775/828089/artclView.do", true),
                                tuple("828034", "[내규] 현장실습 전공학점인정 관련 내규 안내", "2020.03.23", "https://cse.konkuk.ac.kr/bbs/cse/775/828034/artclView.do", true)
                        ),
                () -> assertThat(normal).hasSize(10),
                () -> assertThat(normal)
                        .extracting("articleId", "subject", "postedDate", "fullUrl", "important")
                        .containsExactlyInAnyOrder(
                                tuple("5744", "[WE人교육센터] 건국대 비교과 프로그램 홍보자료 홍보자료 배포", "2024.02.21", "https://cse.konkuk.ac.kr/bbs/cse/775/5744/artclView.do", false),
                                tuple("5737", "[졸업] 2024학년도 제135회 학위수여식 관련 졸업가능여부 조회 및 행사 안내", "2024.02.16", "https://cse.konkuk.ac.kr/bbs/cse/775/5737/artclView.do", false),
                                tuple("5736", "Adobe(어도비) 네임드 라이선스 회수 및 ETLA 라이선스 사용 안내", "2024.02.15", "https://cse.konkuk.ac.kr/bbs/cse/775/5736/artclView.do", false),
                                tuple("5735", "2024-1학기 학부생 연구인턴 프로그램 시행 안내", "2024.02.14", "https://cse.konkuk.ac.kr/bbs/cse/775/5735/artclView.do", false),
                                tuple("828684", "[졸업] 2024년 전기 학위복 대여 및 학위기 배부 장소 안내", "2024.02.05", "https://cse.konkuk.ac.kr/bbs/cse/775/828684/artclView.do", false),
                                tuple("828683", "[학사] 2024년도 2월 미졸업자 수강신청학점(미졸코드) 변경 안내", "2024.02.02", "https://cse.konkuk.ac.kr/bbs/cse/775/828683/artclView.do", false),
                                tuple("828682", "[학사] 2024-1학기 대학원 개설 교과목 선수강 신청 안내", "2024.01.31", "https://cse.konkuk.ac.kr/bbs/cse/775/828682/artclView.do", false),
                                tuple("828681", "[학사] 2024학년도 건국대학교 학부 온라인 요람 발간 안내", "2024.01.31", "https://cse.konkuk.ac.kr/bbs/cse/775/828681/artclView.do", false),
                                tuple("828680", "[교직] 교직이수예정자의 교직소양 영역 최저이수기준 안내", "2024.01.31", "https://cse.konkuk.ac.kr/bbs/cse/775/828680/artclView.do", false),
                                tuple("828679", "[학사] 성적평점 백분위 환산점수표 관련 규정 개정 안내", "2024.01.30", "https://cse.konkuk.ac.kr/bbs/cse/775/828679/artclView.do", false)
                        )
        );
    }

    @DisplayName("신규 2024 홈페이지의 학생 공지에서 noticeId가 성공적으로 분석되는지 확인한다")
    @Test
    void LatestPageNoticeHtmlParserNoticeId() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/designid-notice-2024.html"));
        String viewUrl = "https://www.konkuk.ac.kr/bbs/konkuk/4017/{noticeId}/artclView.do";

        // when
        RowsDto rowsDto = new KuisHomepageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList(viewUrl);
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList(viewUrl);

        // then
        assertAll(
                () -> assertThat(important).hasSize(7),
                () -> assertThat(important)
                        .extracting("articleId", "subject", "postedDate", "fullUrl", "important")
                        .containsExactlyInAnyOrder(
                                tuple("5643", "[~8월] 2024 Kyoto Global Design Awards", "2024.02.19", "https://www.konkuk.ac.kr/bbs/konkuk/4017/5643/artclView.do", true),
                                tuple("915679", "[-3.15(금)까지] 2024년 해외인턴지원사업 인턴 디자이너 모집(한국디자인진흥원)", "2024.02.06", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915679/artclView.do", true),
                                tuple("915678", "[재학생 및 신입생] ★2024 건국대학교 가이드북★", "2024.01.22", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915678/artclView.do", true),
                                tuple("915677", "산업디자인학과, 2023 졸업전시 한국디자인진흥원 온라인 특별 기획전 선정", "2023.12.19", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915677/artclView.do", true),
                                tuple("915674", "2023학년도 1학기 기초교양(외국어영역) 교과목 의무이수 면제 시행 안내", "2023.04.06", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915674/artclView.do", true),
                                tuple("915666", "휴·복학 개시 및 주의사항 안내", "2022.12.26", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915666/artclView.do", true),
                                tuple("915663", "[학사팀] 재학생 졸업요건 관리를 위한 필수 확인사항 안내", "2022.11.07", "https://www.konkuk.ac.kr/bbs/konkuk/4017/915663/artclView.do", true)
                        ),
                () -> assertThat(normal).hasSize(10)
        );
    }

    @DisplayName("부동산 학과의 경우 별도의 HTML 파서를 사용하여 공지를 분석한다")
    @Test
    void RealEstateNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/realestate.html"));

        // when
        RowsDto rowsDto = new RealEstateNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important).isEmpty(),
                () -> assertThat(normal).hasSize(15)
        );
    }

    @DisplayName("View Url이 정상적으로 생석되는지 확인한다")
    @Test
    void viewUrlCreate() {
        // given
        String urlTemplate = "https://{department}.konkuk.ac.kr/bbs/{department}/{siteId}/{noticeId}/artclView.do";

        // when
        String viewUrl = urlTemplate
                .replaceAll("\\{department\\}", "cse")
                .replace("{siteId}", "775");

        String result = UriComponentsBuilder.fromUriString(viewUrl)
                .buildAndExpand(5737)
                .toUriString();

        // then
        assertThat(result).isEqualTo("https://cse.konkuk.ac.kr/bbs/cse/775/5737/artclView.do");
    }

    @DisplayName("공지의 articleId가 정상적으로 추출되는지 확인한다")
    @Test
    void extractNoticeFromRow() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/job-notice-2024.html"));
        String viewUrl = "https://www.konkuk.ac.kr/bbs/job/4083/{noticeId}/artclView.do";

        // when
        RowsDto rowsDto = new KuisHomepageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList(viewUrl);
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList(viewUrl);

        // then
        assertAll(
                () -> assertThat(important).hasSize(16),
                () -> assertThat(important)
                        .extracting("articleId", String.class)
                        .containsExactlyInAnyOrder(
                                "1116603",
                                "1116591",
                                "1116531",
                                "1116377",
                                "1116198",
                                "1116185",
                                "1115993",
                                "1115887",
                                "1115881",
                                "1115808",
                                "1115671",
                                "1115611",
                                "1115608",
                                "5529",
                                "5527",
                                "968642"
                        ),
                () -> assertThat(normal).hasSize(10),
                () -> assertThat(normal)
                        .extracting("articleId", String.class)
                        .containsExactlyInAnyOrder(
                                "1117037",
                                "1116988",
                                "1116925",
                                "1116739",
                                "1116659",
                                "1116603",
                                "1116591",
                                "1116565",
                                "1116559",
                                "1116557"
                        )
        );
    }
}

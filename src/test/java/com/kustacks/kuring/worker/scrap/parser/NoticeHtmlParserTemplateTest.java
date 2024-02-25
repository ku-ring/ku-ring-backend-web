package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParserTwo;
import com.kustacks.kuring.worker.scrap.parser.notice.RealEstateNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.RowsDto;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoticeHtmlParserTemplateTest {

    @DisplayName("오래된 학과의 홈페이지 공지를 분석한다")
    @Test
    void LatestPageNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/cse-notice-2024.html"));
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

    @DisplayName("신규 개편된 학과의 홈페이지 공지를 분석한다")
    @Test
    void LatestPageNoticeHtmlParserTwo() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/kbeauty.html"));

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParserTwo().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important).hasSize(28),
                () -> assertThat(normal).hasSize(12)
        );
    }

    @DisplayName("부동산 학과의 경우 별도의 HTML 파서를 사용하여 공지를 분석한다")
    @Test
    void RealEstateNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/realestate.html"));

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

    private static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}

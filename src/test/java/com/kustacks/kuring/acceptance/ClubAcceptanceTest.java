package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.kustacks.kuring.acceptance.ClubStep.동아리_목록_조회_요청;
import static com.kustacks.kuring.acceptance.ClubStep.동아리_목록_조회_응답_확인;
import static com.kustacks.kuring.acceptance.ClubStep.동아리_상세_조회_요청;
import static com.kustacks.kuring.acceptance.ClubStep.동아리_상세_조회_응답_확인;
import static com.kustacks.kuring.acceptance.ClubStep.동아리_소속_조회_응답_확인;
import static com.kustacks.kuring.acceptance.ClubStep.지원하는_동아리_소속_조회_요청;
import static com.kustacks.kuring.acceptance.CommonStep.실패_응답_확인;

@DisplayName("인수 : 동아리")
public class ClubAcceptanceTest extends IntegrationTestSupport {

    @DisplayName("[v2] 서버가 지원하는 동아리 소속 목록을 조회한다")
    @Test
    void look_up_club_divisions() {
        // when
        var 동아리_소속_조회_응답 = 지원하는_동아리_소속_조회_요청();

        // then
        동아리_소속_조회_응답_확인(동아리_소속_조회_응답);
    }

    @DisplayName("[v2] 동아리 목록을 조회한다")
    @Test
    void look_up_club_list() {
        // given
        String category = "academic";
        String division = "central,engineering";

        // when
        var 동아리_목록_조회_응답 = 동아리_목록_조회_요청(category, division);

        // then
        동아리_목록_조회_응답_확인(동아리_목록_조회_응답);
    }

    @DisplayName("[v2] 존재하지 않는 category나 division로 동아리 목록 조회시 실패한다")
    @Test
    void get_clubs_with_invalid_category() {
        var 동아리_목록_조회_응답 = 동아리_목록_조회_요청("invalid-category", "invalid-division");

        실패_응답_확인(동아리_목록_조회_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[v2] 동아리 상세 정보를 조회한다")
    @Test
    void look_up_club_detail() {
        // given
        var 동아리_목록_조회_응답 = 동아리_목록_조회_요청("academic", "central,engineering");
        Long clubId = 동아리_목록_조회_응답.jsonPath().getLong("data.clubs[0].id");

        // when
        var 동아리_상세_조회_응답 = 동아리_상세_조회_요청(clubId);

        // then
        동아리_상세_조회_응답_확인(동아리_상세_조회_응답, clubId);
    }

    @DisplayName("[v2] 존재하지 않는 동아리 상세 조회시 실패한다")
    @Test
    void get_club_detail_not_found() {
        // when
        var 동아리_상세_조회_응답 = 동아리_상세_조회_요청(999999L);

        // then
        실패_응답_확인(동아리_상세_조회_응답, HttpStatus.NOT_FOUND);
    }
}

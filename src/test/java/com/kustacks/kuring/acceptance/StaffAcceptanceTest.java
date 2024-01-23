package com.kustacks.kuring.acceptance;

import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.StaffStep.교직원_조회_요청;
import static com.kustacks.kuring.acceptance.StaffStep.교직원_조회_응답_확인;

@DisplayName("인수 : 교직원")
class StaffAcceptanceTest extends IntegrationTestSupport {

    /**
     * Give : 사전에 저장된 교직원이 있다
     * When : 키워드로 검색하면
     * Then : 해당하는 교직원들이 조회된다
     */
    @Test
    void search_staff_by_keyword() {
        // when
        var 교직원_조회_응답 = 교직원_조회_요청("shine student");

        // then
        교직원_조회_응답_확인(교직원_조회_응답);
    }
}

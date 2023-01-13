package com.kustacks.kuring.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_조회_요청;
import static com.kustacks.kuring.acceptance.CategoryStep.카테고리_조회_요청_응답_확인;

public class CategoryAcceptanceTest extends AcceptanceTest {

    /**
     * Given : 쿠링앱을 실행한다
     * When : 첫 로딩화면 로딩시
     * Then : 상단에 카테고리 목록을 보여준다
     */
    @DisplayName("서버가 지원하는 카테고리 목록을 조회한다.")
    @Test
    public void look_up_category_list() {
        // when
        var 카테고리_조회_요청_응답 = 카테고리_조회_요청();

        // then
        카테고리_조회_요청_응답_확인(카테고리_조회_요청_응답);
    }
}

package com.kustacks.kuring.building.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("도메인 : CampusPlaceCategory")
class CampusPlaceCategoryTest {

    @Test
    @DisplayName("캠퍼스 시설 카테고리를 생성할 수 있다")
    void create_campus_place_category() {
        // when
        CampusPlaceCategory category = new CampusPlaceCategory(
                "  CONVENIENCE_STORE  ",
                "  편의점  ",
                1,
                true
        );

        // then
        assertAll(
                () -> assertThat(category.getCode()).isEqualTo("convenience_store"),
                () -> assertThat(category.getKorName()).isEqualTo("편의점"),
                () -> assertThat(category.getDisplayOrder()).isEqualTo(1),
                () -> assertThat(category.isFilterEnabled()).isTrue()
        );
    }

    @Test
    @DisplayName("카테고리 코드가 없으면 생성할 수 없다")
    void reject_category_without_code() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new CampusPlaceCategory(" ", "편의점", 1, true)
        );
    }

    @Test
    @DisplayName("카테고리 한글명이 없으면 생성할 수 없다")
    void reject_category_without_korean_name() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new CampusPlaceCategory("convenience_store", " ", 1, true)
        );
    }

    @Test
    @DisplayName("카테고리 노출 순서가 양수가 아니면 생성할 수 없다")
    void reject_category_with_invalid_display_order() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new CampusPlaceCategory("convenience_store", "편의점", 0, true)
        );
    }
}

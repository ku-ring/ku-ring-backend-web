package com.kustacks.kuring.building.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("도메인 : CampusPlace")
class CampusPlaceTest {

    @Test
    @DisplayName("캠퍼스 시설을 생성할 수 있다")
    void create_campus_place() {
        // given
        Building building = createBuilding();
        CampusPlaceCategory category = createCategory();

        // when
        CampusPlace campusPlace = createCampusPlace(building, category, 3);

        // then
        assertAll(
                () -> assertThat(campusPlace.getBuilding()).isEqualTo(building),
                () -> assertThat(campusPlace.getCategory()).isEqualTo(category),
                () -> assertThat(campusPlace.getName()).isEqualTo("학생회관 1층 프린터"),
                () -> assertThat(campusPlace.getLocationType()).isEqualTo(CampusPlaceLocationType.INDOOR),
                () -> assertThat(campusPlace.getFloor()).isEqualTo("1F"),
                () -> assertThat(campusPlace.getQuantity()).isEqualTo(3),
                () -> assertThat(campusPlace.getDisplayOrder()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("건물이 없으면 캠퍼스 시설을 생성할 수 없다")
    void reject_campus_place_without_building() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> createCampusPlace(null, createCategory(), 3)
        );
    }

    @Test
    @DisplayName("카테고리가 없으면 캠퍼스 시설을 생성할 수 없다")
    void reject_campus_place_without_category() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> createCampusPlace(createBuilding(), null, 3)
        );
    }

    @Test
    @DisplayName("시설명이 없으면 캠퍼스 시설을 생성할 수 없다")
    void reject_campus_place_without_name() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CampusPlace(
                createBuilding(),
                createCategory(),
                " ",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                null,
                1
        ));
    }

    @Test
    @DisplayName("시설 수량이 양수가 아니면 캠퍼스 시설을 생성할 수 없다")
    void reject_campus_place_with_invalid_quantity() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> createCampusPlace(createBuilding(), createCategory(), 0)
        );
    }

    @Test
    @DisplayName("운영시간을 추가할 수 있다")
    void add_operating_hours() {
        // given
        CampusPlace campusPlace = createCampusPlace(createBuilding(), createCategory(), 3);
        OperatingHours operatingHours = createOperatingHours();

        // when
        campusPlace.addOperatingHours(operatingHours);

        // then
        assertThat(campusPlace.getOperatingHours()).containsExactly(operatingHours);
    }

    @Test
    @DisplayName("같은 기간과 요일의 운영시간은 중복으로 추가할 수 없다")
    void reject_duplicated_operating_hours() {
        // given
        CampusPlace campusPlace = createCampusPlace(createBuilding(), createCategory(), 3);
        campusPlace.addOperatingHours(createOperatingHours());
        OperatingHours duplicatedHours = new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.OPEN_24_HOURS,
                null,
                null
        );

        // when, then
        assertThatIllegalArgumentException().isThrownBy(
                () -> campusPlace.addOperatingHours(duplicatedHours)
        );
    }

    private Building createBuilding() {
        return new Building("학생회관", 37.5412, 127.0784);
    }

    private CampusPlaceCategory createCategory() {
        return new CampusPlaceCategory("printer", "프린터", 1, true);
    }

    private CampusPlace createCampusPlace(
            Building building,
            CampusPlaceCategory category,
            Integer quantity
    ) {
        return new CampusPlace(
                building,
                category,
                "학생회관 1층 프린터",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                quantity,
                "https://example.com",
                1
        );
    }

    private OperatingHours createOperatingHours() {
        return new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );
    }
}

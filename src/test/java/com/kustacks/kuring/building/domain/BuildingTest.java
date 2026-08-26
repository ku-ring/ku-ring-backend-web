package com.kustacks.kuring.building.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("도메인 : Building")
class BuildingTest {

    @Test
    @DisplayName("Building을 생성할 수 있다")
    void create_building() {
        // given
        String name = "공학관";
        Double lat = 37.5401;
        Double lon = 127.0784;

        // when
        Building building = new Building(name, lat, lon);

        // then
        assertAll(
                () -> assertThat(building.getName()).isEqualTo(name),
                () -> assertThat(building.getAddress()).isEqualTo("서울특별시 광진구 능동로 120"),
                () -> assertThat(building.getLat()).isEqualTo(lat),
                () -> assertThat(building.getLon()).isEqualTo(lon)
        );
    }

    @Test
    @DisplayName("건물명과 주소는 필수이다")
    void reject_building_without_required_fields() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> new Building(
                        " ",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784,
                        null
                )),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> new Building(
                        "학생회관",
                        " ",
                        37.5412,
                        127.0784,
                        null
                ))
        );
    }

    @Test
    @DisplayName("건물 검색어를 추가할 수 있다")
    void add_search_keyword() {
        // given
        Building building = createBuilding();

        // when
        building.addSearchKeyword("  학관  ");

        // then
        assertThat(building.getKeywords())
                .singleElement()
                .extracting(BuildingSearchKeyword::getKeyword)
                .isEqualTo("학관");
    }

    @Test
    @DisplayName("건물 검색어가 없으면 추가할 수 없다")
    void reject_empty_search_keyword() {
        Building building = createBuilding();

        assertThatIllegalArgumentException().isThrownBy(() -> building.addSearchKeyword(" "));
    }

    @Test
    @DisplayName("캠퍼스 시설을 추가할 수 있다")
    void add_campus_place() {
        // given
        Building building = createBuilding();
        CampusPlace campusPlace = createCampusPlace(building);

        // when
        building.addCampusPlace(campusPlace);

        // then
        assertThat(building.getCampusPlaces()).containsExactly(campusPlace);
    }

    @Test
    @DisplayName("캠퍼스 시설이 없으면 추가할 수 없다")
    void reject_null_campus_place() {
        Building building = createBuilding();

        assertThatIllegalArgumentException().isThrownBy(() -> building.addCampusPlace(null));
    }

    @Test
    @DisplayName("운영시간을 추가할 수 있다")
    void add_operating_hours() {
        // given
        Building building = createBuilding();
        OperatingHours operatingHours = createOperatingHours();

        // when
        building.addOperatingHours(operatingHours);

        // then
        assertThat(building.getOperatingHours()).containsExactly(operatingHours);
    }

    @Test
    @DisplayName("같은 기간과 요일의 운영시간은 중복으로 추가할 수 없다")
    void reject_duplicated_operating_hours() {
        // given
        Building building = createBuilding();
        building.addOperatingHours(createOperatingHours());
        OperatingHours duplicatedHours = new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.OPEN_24_HOURS,
                null,
                null
        );

        // when, then
        assertThatIllegalArgumentException().isThrownBy(
                () -> building.addOperatingHours(duplicatedHours)
        );
    }

    private Building createBuilding() {
        return new Building("학생회관", 37.5412, 127.0784);
    }

    private CampusPlace createCampusPlace(Building building) {
        return new CampusPlace(
                building,
                new CampusPlaceCategory("printer", "프린터", 1, true),
                "학생회관 1층 프린터",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                null,
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

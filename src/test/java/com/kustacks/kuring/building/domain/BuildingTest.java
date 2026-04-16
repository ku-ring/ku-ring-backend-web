package com.kustacks.kuring.building.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
                () -> assertThat(building.getLat()).isEqualTo(lat),
                () -> assertThat(building.getLon()).isEqualTo(lon)
        );
    }
}

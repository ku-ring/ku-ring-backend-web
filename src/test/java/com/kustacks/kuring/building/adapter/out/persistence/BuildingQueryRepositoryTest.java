package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("리포지토리 : BuildingQueryRepository")
class BuildingQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private BuildingRepository buildingRepository;

    @Test
    @DisplayName("건물명, 주소, 등록된 검색어로 건물을 검색한다")
    void search_buildings_by_name_address_and_keyword() {
        // given
        Building lawBuilding = new Building(
                "법학관",
                "서울특별시 광진구 능동로 120",
                37.54174,
                127.07649,
                null
        );
        lawBuilding.addSearchKeyword("종강");
        buildingRepository.saveAndFlush(lawBuilding);

        // when & then
        assertAll(
                () -> assertThat(buildingRepository.searchByKeyword("법학"))
                        .extracting(Building::getName)
                        .containsExactly("법학관"),
                () -> assertThat(buildingRepository.searchByKeyword("능동로"))
                        .extracting(Building::getName)
                        .containsExactly("법학관"),
                () -> assertThat(buildingRepository.searchByKeyword("종강"))
                        .extracting(Building::getName)
                        .containsExactly("법학관")
        );
    }

    @Test
    @DisplayName("LIKE 와일드카드를 일반 문자로 검색한다")
    void search_like_wildcards_as_literal_characters() {
        // given
        Building percentBuilding = building("100%관");
        Building underscoreBuilding = building("A_B관");
        Building backslashBuilding = building("A\\B관");
        Building normalBuilding = building("일반관");
        buildingRepository.saveAllAndFlush(List.of(
                percentBuilding,
                underscoreBuilding,
                backslashBuilding,
                normalBuilding
        ));

        // when & then
        assertAll(
                () -> assertThat(buildingRepository.searchByKeyword("%"))
                        .extracting(Building::getName)
                        .containsExactly("100%관"),
                () -> assertThat(buildingRepository.searchByKeyword("_"))
                        .extracting(Building::getName)
                        .containsExactly("A_B관"),
                () -> assertThat(buildingRepository.searchByKeyword("\\"))
                        .extracting(Building::getName)
                        .containsExactly("A\\B관")
        );
    }

    private Building building(String name) {
        return new Building(
                name,
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        );
    }
}

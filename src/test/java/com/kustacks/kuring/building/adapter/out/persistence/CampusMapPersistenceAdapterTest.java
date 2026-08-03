package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("영속성 어댑터 : CampusMapPersistenceAdapter")
@ExtendWith(MockitoExtension.class)
class CampusMapPersistenceAdapterTest {

    @Mock
    private CampusPlaceCategoryRepository categoryRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private CampusMapPersistenceAdapter campusMapPersistenceAdapter;

    @Test
    @DisplayName("필터에 노출되는 카테고리를 노출 순서대로 조회한다")
    void find_filter_categories() {
        // given
        List<CampusPlaceCategory> categories = List.of(
                new CampusPlaceCategory("cafe", "카페", 1, true),
                new CampusPlaceCategory("restaurant", "식당", 2, true)
        );
        when(categoryRepository.findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc())
                .thenReturn(categories);

        // when
        List<CampusPlaceCategoryReadModel> result = campusMapPersistenceAdapter.findFilterCategories();

        // then
        assertThat(result).containsExactly(
                new CampusPlaceCategoryReadModel("cafe", "카페", 1),
                new CampusPlaceCategoryReadModel("restaurant", "식당", 2)
        );
        verify(categoryRepository).findByFilterEnabledTrueOrderByDisplayOrderAscIdAsc();
    }

    @Test
    @DisplayName("캠퍼스 건물을 ID 순서대로 조회한다")
    void find_buildings() {
        // given
        Building administration = building(1L, "행정관", 37.54241, 127.07382);
        Building business = building(2L, "경영관", 37.54196, 127.07531);
        when(buildingRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(administration, business));

        // when
        List<BuildingSummaryReadModel> result = campusMapPersistenceAdapter.findBuildings();

        // then
        assertThat(result).containsExactly(
                new BuildingSummaryReadModel(
                        1L,
                        "행정관",
                        "서울특별시 광진구 능동로 120",
                        37.54241,
                        127.07382
                ),
                new BuildingSummaryReadModel(
                        2L,
                        "경영관",
                        "서울특별시 광진구 능동로 120",
                        37.54196,
                        127.07531
                )
        );
        verify(buildingRepository).findAllByOrderByIdAsc();
    }

    private Building building(Long id, String name, Double latitude, Double longitude) {
        Building building = new Building(
                name,
                "서울특별시 광진구 능동로 120",
                latitude,
                longitude,
                null
        );
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }
}

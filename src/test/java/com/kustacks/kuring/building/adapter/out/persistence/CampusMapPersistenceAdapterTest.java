package com.kustacks.kuring.building.adapter.out.persistence;

import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceReadModel;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHours;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("영속성 어댑터 : CampusMapPersistenceAdapter")
@ExtendWith(MockitoExtension.class)
class CampusMapPersistenceAdapterTest {

    @Mock
    private CampusPlaceCategoryRepository categoryRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private CampusPlaceRepository campusPlaceRepository;

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
    @DisplayName("캠퍼스 건물을 노출 우선순위대로 조회한다")
    void find_buildings() {
        // given
        Building administration = building(1L, "행정관", 37.54241, 127.07382);
        Building business = building(2L, "경영관", 37.54196, 127.07531);
        when(buildingRepository.findAllSortedByDisplayOrder())
                .thenReturn(List.of(administration, business));

        // when
        List<BuildingSummaryReadModel> result = campusMapPersistenceAdapter.findBuildings();

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).name()).isEqualTo("행정관"),
                () -> assertThat(result.get(0).displayOrder()).isEqualTo(1),
                () -> assertThat(result.get(1).name()).isEqualTo("경영관"),
                () -> assertThat(result.get(1).displayOrder()).isEqualTo(2),
                () -> verify(buildingRepository).findAllSortedByDisplayOrder()
        );
    }

    @Test
    @DisplayName("건물 검색 결과를 조회한다")
    void search_buildings() {
        // given
        Building studentCenter = building(4L, "학생회관", 37.5412, 127.0784);
        when(buildingRepository.searchByKeyword("학관"))
                .thenReturn(List.of(studentCenter));

        // when
        List<BuildingSummaryReadModel> result = campusMapPersistenceAdapter.searchBuildings("학관");

        // then
        assertThat(result).containsExactly(
                new BuildingSummaryReadModel(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784,
                        4
                )
        );
        verify(buildingRepository).searchByKeyword("학관");
    }

    @Test
    @DisplayName("선택한 카테고리에 해당하는 캠퍼스 시설을 조회한다")
    void find_campus_places_by_categories() {
        // given
        Building studentCenter = building(4L, "학생회관", 37.5412, 127.0784);
        CampusPlaceCategory category = new CampusPlaceCategory("printer", "프린터", 1, true);
        CampusPlace printer = new CampusPlace(
                studentCenter,
                category,
                "학생회관 프린터",
                "campus-map/printer.png",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                null,
                1
        );
        ReflectionTestUtils.setField(printer, "id", 10L);
        printer.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        ));
        when(campusPlaceRepository.findByFilterCategories(List.of("printer")))
                .thenReturn(List.of(printer));

        // when
        List<CampusPlaceReadModel> result = campusMapPersistenceAdapter.findCampusPlacesByCategories(
                List.of("printer")
        );

        // then
        assertThat(result).singleElement().satisfies(place -> {
            assertAll(
                    () -> assertThat(place.name()).isEqualTo("학생회관 프린터"),
                    () -> assertThat(place.categoryCode()).isEqualTo("printer"),
                    () -> assertThat(place.operatingHours()).hasSize(1),
                    () -> assertThat(place.building().name()).isEqualTo("학생회관")
            );
        });
        verify(campusPlaceRepository).findByFilterCategories(List.of("printer"));
    }

    @Test
    @DisplayName("카테고리가 비어 있으면 캠퍼스 시설을 조회하지 않는다")
    void return_empty_campus_places_when_categories_are_empty() {
        // when
        List<CampusPlaceReadModel> result = campusMapPersistenceAdapter.findCampusPlacesByCategories(List.of());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("건물 상세 정보를 조회한다")
    void find_building_by_id() {
        // given
        Building studentCenter = building(4L, "학생회관", 37.5412, 127.0784);
        studentCenter.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        ));
        when(buildingRepository.findById(4L)).thenReturn(java.util.Optional.of(studentCenter));

        // when
        var result = campusMapPersistenceAdapter.findBuildingById(4L);

        // then
        assertThat(result)
                .hasValueSatisfying(building -> assertAll(
                        () -> assertThat(building.name()).isEqualTo("학생회관"),
                        () -> assertThat(building.operatingHours()).hasSize(1)
                ));
        verify(buildingRepository).findById(4L);
    }

    @Test
    @DisplayName("건물에 등록된 캠퍼스 시설을 조회한다")
    void find_campus_places_by_building_id() {
        // given
        Building studentCenter = building(4L, "학생회관", 37.5412, 127.0784);
        CampusPlaceCategory category = new CampusPlaceCategory("printer", "프린터", 1, true);
        CampusPlace printer = new CampusPlace(
                studentCenter,
                category,
                "학생회관 프린터",
                null,
                CampusPlaceLocationType.INDOOR,
                "1F",
                null,
                3,
                null,
                1
        );
        when(campusPlaceRepository.findByBuildingId(4L)).thenReturn(List.of(printer));

        // when
        List<CampusPlaceReadModel> result = campusMapPersistenceAdapter.findCampusPlacesByBuildingId(4L);

        // then
        assertThat(result)
                .singleElement()
                .satisfies(place -> assertAll(
                        () -> assertThat(place.name()).isEqualTo("학생회관 프린터"),
                        () -> assertThat(place.building().id()).isEqualTo(4L)
                ));
        verify(campusPlaceRepository).findByBuildingId(4L);
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
        ReflectionTestUtils.setField(building, "displayOrder", id.intValue());
        return building;
    }
}

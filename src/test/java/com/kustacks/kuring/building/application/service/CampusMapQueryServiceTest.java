package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.out.AcademicPeriodPort;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.domain.Building;
import com.kustacks.kuring.building.domain.CampusPlace;
import com.kustacks.kuring.building.domain.CampusPlaceCategory;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHours;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : CampusMapQueryService")
@ExtendWith(MockitoExtension.class)
class CampusMapQueryServiceTest {

    private static final Clock MONDAY_CLOCK = Clock.fixed(
            Instant.parse("2026-07-20T00:00:00Z"),
            ZoneId.of("Asia/Seoul")
    );

    @Mock
    private CampusMapQueryPort campusMapQueryPort;

    @Mock
    private AcademicPeriodPort academicPeriodPort;

    @Mock
    private StoragePort storagePort;

    private CampusMapQueryService service;

    @BeforeEach
    void setUp() {
        service = new CampusMapQueryService(
                campusMapQueryPort,
                academicPeriodPort,
                storagePort,
                MONDAY_CLOCK
        );
    }

    @Test
    @DisplayName("건물 검색어의 앞뒤 공백을 제거하여 조회한다")
    void search_buildings_with_trimmed_keyword() {
        // given
        Building building = building();
        when(campusMapQueryPort.searchBuildings("학관")).thenReturn(List.of(building));

        // when
        List<BuildingSummaryResult> results = service.searchBuildings("  학관  ");

        // then
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0).name()).isEqualTo("학생회관"),
                () -> assertThat(results.get(0).address()).isEqualTo("서울특별시 광진구 능동로 120")
        );
        verify(campusMapQueryPort).searchBuildings("학관");
    }

    @Test
    @DisplayName("복수 카테고리를 정규화하고 현재 기간의 운영시간이 없으면 UNKNOWN을 반환한다")
    void get_campus_places_with_normalized_categories_and_unknown_hours() {
        // given
        Building building = building();
        CampusPlaceCategory category = new CampusPlaceCategory("printer", "프린터", 1, true);
        CampusPlace place = new CampusPlace(
                building,
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
        ReflectionTestUtils.setField(place, "id", 10L);
        place.addOperatingHours(new OperatingHours(
                OperatingPeriod.SEMESTER,
                OperatingDayGroup.WEEKDAY,
                OperatingHoursStatus.SCHEDULED,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        ));

        when(academicPeriodPort.resolve(MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate()))
                .thenReturn(OperatingPeriod.VACATION);
        when(campusMapQueryPort.findCampusPlacesByCategories(List.of("printer", "cafe")))
                .thenReturn(List.of(place));
        when(storagePort.getPresignedUrl("campus-map/printer.png"))
                .thenReturn("https://storage.example.com/printer.png");

        // when
        List<CampusPlaceResult> results = service.getCampusPlaces(
                List.of(" Printer, cafe ", "printer")
        );

        // then
        CampusPlaceResult result = results.get(0);
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(result.imageUrl()).isEqualTo("https://storage.example.com/printer.png"),
                () -> assertThat(result.currentOperatingHours().period()).isEqualTo(OperatingPeriod.VACATION),
                () -> assertThat(result.currentOperatingHours().dayGroup()).isEqualTo(OperatingDayGroup.WEEKDAY),
                () -> assertThat(result.currentOperatingHours().status()).isEqualTo(OperatingHoursStatus.UNKNOWN),
                () -> assertThat(result.currentOperatingHours().opensAt()).isNull(),
                () -> assertThat(result.currentOperatingHours().closesAt()).isNull()
        );
        verify(campusMapQueryPort).findCampusPlacesByCategories(List.of("printer", "cafe"));
    }

    @Test
    @DisplayName("운영시간이 없으면 현재 기간과 요일에 대해 UNKNOWN을 반환한다")
    void get_building_detail_with_unknown_operating_hours() {
        // given
        Building building = building();
        when(campusMapQueryPort.findBuildingById(1L)).thenReturn(Optional.of(building));
        when(campusMapQueryPort.findCampusPlacesByBuildingId(1L)).thenReturn(List.of());
        when(academicPeriodPort.resolve(MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate()))
                .thenReturn(OperatingPeriod.VACATION);

        // when
        BuildingDetailResult result = service.getBuildingDetail(1L).orElseThrow();

        // then
        assertAll(
                () -> assertThat(result.currentOperatingHours().period()).isEqualTo(OperatingPeriod.VACATION),
                () -> assertThat(result.currentOperatingHours().dayGroup()).isEqualTo(OperatingDayGroup.WEEKDAY),
                () -> assertThat(result.currentOperatingHours().status()).isEqualTo(OperatingHoursStatus.UNKNOWN),
                () -> assertThat(result.currentOperatingHours().opensAt()).isNull(),
                () -> assertThat(result.currentOperatingHours().closesAt()).isNull(),
                () -> assertThat(result.campusPlaces()).isEmpty()
        );
    }

    @Test
    @DisplayName("건물이 없으면 시설과 현재 운영기간을 조회하지 않는다")
    void get_building_detail_when_building_does_not_exist() {
        // given
        when(campusMapQueryPort.findBuildingById(999L)).thenReturn(Optional.empty());

        // when
        Optional<BuildingDetailResult> result = service.getBuildingDetail(999L);

        // then
        assertThat(result).isEmpty();
        verify(campusMapQueryPort, never()).findCampusPlacesByBuildingId(999L);
        verify(academicPeriodPort, never()).resolve(MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate());
    }

    private Building building() {
        Building building = new Building(
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                null
        );
        ReflectionTestUtils.setField(building, "id", 1L);
        return building;
    }
}

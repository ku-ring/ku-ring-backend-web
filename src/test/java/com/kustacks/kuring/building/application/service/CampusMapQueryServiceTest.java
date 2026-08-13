package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingOverviewResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.out.AcademicPeriodQueryPort;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingDetailReadModel;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceReadModel;
import com.kustacks.kuring.building.application.port.out.dto.OperatingHoursReadModel;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    private AcademicPeriodQueryPort academicPeriodQueryPort;

    @Mock
    private StoragePort storagePort;

    private CampusMapQueryService campusMapQueryService;

    @BeforeEach
    void setUp() {
        campusMapQueryService = new CampusMapQueryService(
                campusMapQueryPort,
                academicPeriodQueryPort,
                storagePort,
                MONDAY_CLOCK
        );
    }

    @Test
    @DisplayName("필터에 노출되는 캠퍼스맵 카테고리 목록을 조회한다")
    void get_categories() {
        // given
        when(campusMapQueryPort.findFilterCategories()).thenReturn(List.of(
                new CampusPlaceCategoryReadModel("cafe", "카페", 1),
                new CampusPlaceCategoryReadModel("restaurant", "식당", 2)
        ));

        // when
        List<CategoryResult> result = campusMapQueryService.getCategories();

        // then
        assertThat(result).containsExactly(
                new CategoryResult("cafe", "카페", 1),
                new CategoryResult("restaurant", "식당", 2)
        );
        verify(campusMapQueryPort).findFilterCategories();
    }

    @Test
    @DisplayName("캠퍼스맵 전체 건물 목록을 조회한다")
    void get_buildings() {
        // given
        when(campusMapQueryPort.findBuildings()).thenReturn(List.of(
                new BuildingSummaryReadModel(
                        1L,
                        "행정관",
                        "서울특별시 광진구 능동로 120",
                        37.54241,
                        127.07382,
                        1
                ),
                new BuildingSummaryReadModel(
                        2L,
                        "경영관",
                        "서울특별시 광진구 능동로 120",
                        37.54196,
                        127.07531,
                        2
                )
        ));

        // when
        List<BuildingOverviewResult> result = campusMapQueryService.getBuildings();

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).name()).isEqualTo("행정관"),
                () -> assertThat(result.get(0).displayOrder()).isEqualTo(1),
                () -> assertThat(result.get(1).name()).isEqualTo("경영관"),
                () -> assertThat(result.get(1).displayOrder()).isEqualTo(2),
                () -> verify(campusMapQueryPort).findBuildings()
        );
    }

    @Test
    @DisplayName("건물 검색어의 앞뒤 공백을 제거하여 조회한다")
    void search_buildings_with_trimmed_keyword() {
        // given
        when(campusMapQueryPort.searchBuildings("학관")).thenReturn(List.of(
                new BuildingSummaryReadModel(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784,
                        4
                )
        ));

        // when
        List<BuildingSummaryResult> result = campusMapQueryService.searchBuildings("  학관  ");

        // then
        assertThat(result).containsExactly(
                new BuildingSummaryResult(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784
                )
        );
        verify(campusMapQueryPort).searchBuildings("학관");
    }

    @Test
    @DisplayName("시설 카테고리의 앞뒤 공백을 제거하여 조회한다")
    void get_campus_places_with_trimmed_category() {
        // given
        givenCurrentOperatingPeriod();

        // when
        campusMapQueryService.getCampusPlaces(List.of(" printer "));

        // then
        verify(campusMapQueryPort).findCampusPlacesByCategories(List.of("printer"));
    }

    @Test
    @DisplayName("시설 카테고리를 소문자로 변환하여 조회한다")
    void get_campus_places_with_lowercase_category() {
        // given
        givenCurrentOperatingPeriod();

        // when
        campusMapQueryService.getCampusPlaces(List.of("PRINTER"));

        // then
        verify(campusMapQueryPort).findCampusPlacesByCategories(List.of("printer"));
    }

    @Test
    @DisplayName("중복된 시설 카테고리를 제거하여 조회한다")
    void get_campus_places_with_distinct_categories() {
        // given
        givenCurrentOperatingPeriod();

        // when
        campusMapQueryService.getCampusPlaces(List.of("printer", "printer"));

        // then
        verify(campusMapQueryPort).findCampusPlacesByCategories(List.of("printer"));
    }

    @Test
    @DisplayName("시설의 전체 운영시간과 현재 적용 여부를 반환한다")
    void get_campus_places_with_operating_hours() {
        // given
        CampusPlaceReadModel place = campusPlaceReadModel();
        when(academicPeriodQueryPort.determineOperatingPeriod(
                MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate()
        )).thenReturn(OperatingPeriod.VACATION);
        when(campusMapQueryPort.findCampusPlacesByCategories(List.of("printer")))
                .thenReturn(List.of(place));
        when(storagePort.getPresignedUrl("campus-map/student-center.png"))
                .thenReturn("https://storage.example.com/student-center.png");

        // when
        List<CampusPlaceResult> results = campusMapQueryService.getCampusPlaces(
                List.of("printer")
        );

        // then
        assertThat(results).hasSize(1);
        CampusPlaceResult result = results.get(0);
        assertAll(
                () -> assertThat(result.imageUrl()).isEqualTo("https://storage.example.com/student-center.png"),
                () -> assertThat(result.operatingHours()).hasSize(2),
                () -> assertThat(result.operatingHours().get(0).period())
                        .isEqualTo(OperatingPeriod.SEMESTER),
                () -> assertThat(result.operatingHours().get(0).isCurrent()).isFalse(),
                () -> assertThat(result.operatingHours().get(1).period())
                        .isEqualTo(OperatingPeriod.VACATION),
                () -> assertThat(result.operatingHours().get(1).dayGroup())
                        .isEqualTo(OperatingDayGroup.WEEKDAY),
                () -> assertThat(result.operatingHours().get(1).status())
                        .isEqualTo(OperatingHoursStatus.OPEN_24_HOURS),
                () -> assertThat(result.operatingHours().get(1).isCurrent()).isTrue()
        );
        verify(campusMapQueryPort).findCampusPlacesByCategories(List.of("printer"));
    }

    @Test
    @DisplayName("건물 상세 정보와 등록된 시설을 조회한다")
    void get_building_detail() {
        // given
        BuildingDetailReadModel building = new BuildingDetailReadModel(
                4L,
                "학생회관",
                "서울특별시 광진구 능동로 120",
                37.5412,
                127.0784,
                "campus-map/student-center.png",
                List.of()
        );
        when(campusMapQueryPort.findBuildingById(4L)).thenReturn(Optional.of(building));
        when(campusMapQueryPort.findCampusPlacesByBuildingId(4L))
                .thenReturn(List.of(campusPlaceReadModel()));
        when(academicPeriodQueryPort.determineOperatingPeriod(
                MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate()
        )).thenReturn(OperatingPeriod.VACATION);
        when(storagePort.getPresignedUrl("campus-map/student-center.png"))
                .thenReturn("https://storage.example.com/student-center.png");

        // when
        BuildingDetailResult result = campusMapQueryService.getBuildingDetail(4L);

        // then
        assertThat(result.campusPlaces()).hasSize(1);
        CampusPlaceResult campusPlace = result.campusPlaces().get(0);
        assertAll(
                () -> assertThat(result.name()).isEqualTo("학생회관"),
                () -> assertThat(result.imageUrl())
                        .isEqualTo("https://storage.example.com/student-center.png"),
                () -> assertThat(campusPlace.name()).isEqualTo("학생회관 프린터"),
                () -> assertThat(campusPlace.imageUrl())
                        .isEqualTo("https://storage.example.com/student-center.png")
        );
        verify(campusMapQueryPort).findCampusPlacesByBuildingId(4L);
    }

    @Test
    @DisplayName("존재하지 않는 건물 상세 조회 시 예외를 발생시킨다")
    void get_building_detail_not_found() {
        // given
        when(campusMapQueryPort.findBuildingById(999L)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> campusMapQueryService.getBuildingDetail(999L))
                .isInstanceOf(NotFoundException.class)
                .extracting(exception -> ((NotFoundException) exception).getErrorCode())
                .isEqualTo(ErrorCode.BUILDING_NOT_FOUND);
        verify(campusMapQueryPort).findBuildingById(999L);
    }

    private void givenCurrentOperatingPeriod() {
        when(academicPeriodQueryPort.determineOperatingPeriod(
                MONDAY_CLOCK.instant().atZone(MONDAY_CLOCK.getZone()).toLocalDate()
        )).thenReturn(OperatingPeriod.VACATION);
    }

    private CampusPlaceReadModel campusPlaceReadModel() {
        return new CampusPlaceReadModel(
                10L,
                "학생회관 프린터",
                "printer",
                "프린터",
                "campus-map/student-center.png",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                List.of(
                        new OperatingHoursReadModel(
                                OperatingPeriod.SEMESTER,
                                OperatingDayGroup.WEEKDAY,
                                OperatingHoursStatus.SCHEDULED,
                                LocalTime.of(8, 0),
                                LocalTime.of(22, 0)
                        ),
                        new OperatingHoursReadModel(
                                OperatingPeriod.VACATION,
                                OperatingDayGroup.WEEKDAY,
                                OperatingHoursStatus.OPEN_24_HOURS,
                                null,
                                null
                        )
                ),
                null,
                new BuildingSummaryReadModel(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784,
                        4
                )
        );
    }
}

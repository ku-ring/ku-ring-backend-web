package com.kustacks.kuring.building.adapter.in.web;

import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingOverview;
import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryDto;
import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingOverviewResult;
import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusPlaceResult;
import com.kustacks.kuring.building.application.port.in.dto.CampusMapSearchResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.in.dto.OperatingHoursResult;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import com.kustacks.kuring.common.dto.BaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("컨트롤러 : CampusMapQueryApiV2")
@ExtendWith(MockitoExtension.class)
class CampusMapQueryApiV2Test {

    @Mock
    private CampusMapQueryUseCase campusMapQueryUseCase;

    @InjectMocks
    private CampusMapQueryApiV2 campusMapQueryApiV2;

    @Test
    @DisplayName("캠퍼스맵 카테고리 목록을 조회한다")
    void get_categories() {
        // given
        when(campusMapQueryUseCase.getCategories()).thenReturn(List.of(
                new CategoryResult("cafe", "카페", 1),
                new CategoryResult("restaurant", "식당", 2)
        ));

        // when
        var response = campusMapQueryApiV2.getCategories();

        // then
        var body = response.getBody();
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(body)
                        .extracting(BaseResponse::getCode, BaseResponse::getMessage)
                        .containsExactly(200, "장소 카테고리 목록 조회에 성공하였습니다"),
                () -> assertThat(body.getData().categories())
                        .extracting(
                                CategoryDto::name,
                                CategoryDto::korName,
                                CategoryDto::displayOrder
                        )
                        .containsExactly(
                                tuple("cafe", "카페", 1),
                                tuple("restaurant", "식당", 2)
                        )
        );
    }

    @Test
    @DisplayName("캠퍼스맵 전체 건물 목록을 조회한다")
    void get_buildings() {
        // given
        when(campusMapQueryUseCase.getBuildings()).thenReturn(List.of(
                new BuildingOverviewResult(
                        1L,
                        "행정관",
                        "서울특별시 광진구 능동로 120",
                        37.54241,
                        127.07382,
                        1
                ),
                new BuildingOverviewResult(
                        2L,
                        "경영관",
                        "서울특별시 광진구 능동로 120",
                        37.54196,
                        127.07531,
                        2
                )
        ));

        // when
        var response = campusMapQueryApiV2.getBuildings();

        // then
        var body = response.getBody();
        assertThat(body).isNotNull();
        List<BuildingOverview> buildings = body.getData().buildings();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(body.getCode()).isEqualTo(200),
                () -> assertThat(body.getMessage()).isEqualTo("캠퍼스 건물 목록 조회에 성공하였습니다"),
                () -> assertThat(buildings).hasSize(2),
                () -> assertThat(buildings.get(0).id()).isEqualTo(1L),
                () -> assertThat(buildings.get(0).name()).isEqualTo("행정관"),
                () -> assertThat(buildings.get(0).address()).isEqualTo("서울특별시 광진구 능동로 120"),
                () -> assertThat(buildings.get(0).displayOrder()).isEqualTo(1),
                () -> assertThat(buildings.get(1).id()).isEqualTo(2L),
                () -> assertThat(buildings.get(1).name()).isEqualTo("경영관"),
                () -> assertThat(buildings.get(1).address()).isEqualTo("서울특별시 광진구 능동로 120"),
                () -> assertThat(buildings.get(1).displayOrder()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("캠퍼스맵 건물과 시설을 키워드로 검색한다")
    void search_campus_map() {
        // given
        when(campusMapQueryUseCase.searchCampusMap("학관")).thenReturn(
                new CampusMapSearchResult(
                        List.of(new BuildingSummaryResult(
                                4L,
                                "학생회관",
                                "서울특별시 광진구 능동로 120",
                                37.5412,
                                127.0784
                        )),
                        List.of(campusPlaceResult())
                )
        );

        // when
        var response = campusMapQueryApiV2.searchCampusMap("학관");

        // then
        var body = response.getBody();
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(body.getCode()).isEqualTo(200),
                () -> assertThat(body.getMessage()).isEqualTo("캠퍼스맵 검색에 성공하였습니다"),
                () -> assertThat(body.getData().buildings())
                        .extracting(
                                BuildingSummary::id,
                                BuildingSummary::name,
                                BuildingSummary::address
                        )
                        .containsExactly(
                                tuple(4L, "학생회관", "서울특별시 광진구 능동로 120")
                        ),
                () -> assertThat(body.getData().campusPlaces()).hasSize(1),
                () -> assertThat(body.getData().campusPlaces().get(0).name())
                        .isEqualTo("학생회관 프린터")
        );
    }

    @Test
    @DisplayName("카테고리 기반 캠퍼스 시설 목록을 조회한다")
    void get_campus_places() {
        // given
        when(campusMapQueryUseCase.getCampusPlaces(List.of("printer")))
                .thenReturn(List.of(campusPlaceResult()));

        // when
        var response = campusMapQueryApiV2.getCampusPlaces(List.of("printer"));

        // then
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(body)
                        .extracting(BaseResponse::getCode, BaseResponse::getMessage)
                        .containsExactly(200, "카테고리 기반 시설 목록 조회에 성공하였습니다"),
                () -> assertThat(body.getData().campusPlaces())
                        .singleElement()
                        .satisfies(campusPlace -> assertAll(
                                () -> assertThat(campusPlace.name()).isEqualTo("학생회관 프린터"),
                                () -> assertThat(campusPlace.operatingHours().get(0).isCurrent()).isTrue(),
                                () -> assertThat(campusPlace.operatingHours().get(0).opensAt()).isEqualTo("08:00"),
                                () -> assertThat(campusPlace.building().name()).isEqualTo("학생회관")
                        ))
        );
    }

    @Test
    @DisplayName("캠퍼스맵 건물 상세 정보를 조회한다")
    void get_building_detail() {
        // given
        when(campusMapQueryUseCase.getBuildingDetail(4L)).thenReturn(
                new BuildingDetailResult(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784,
                        "https://storage.example.com/student-center.png",
                        List.of(),
                        List.of()
                )
        );

        // when
        var response = campusMapQueryApiV2.getBuildingDetail(4L);

        // then
        var body = response.getBody();
        assertThat(body).isNotNull();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(body)
                        .extracting(BaseResponse::getCode, BaseResponse::getMessage)
                        .containsExactly(200, "캠퍼스 건물 상세 조회에 성공하였습니다"),
                () -> assertThat(body.getData().name()).isEqualTo("학생회관")
        );
    }

    private CampusPlaceResult campusPlaceResult() {
        return new CampusPlaceResult(
                10L,
                "학생회관 프린터",
                "printer",
                "프린터",
                "https://storage.example.com/printer.png",
                CampusPlaceLocationType.INDOOR,
                "1F",
                "라운지 안쪽",
                3,
                List.of(new OperatingHoursResult(
                        OperatingPeriod.SEMESTER,
                        OperatingDayGroup.WEEKDAY,
                        OperatingHoursStatus.SCHEDULED,
                        LocalTime.of(8, 0),
                        LocalTime.of(22, 0),
                        true
                )),
                null,
                new BuildingSummaryResult(
                        4L,
                        "학생회관",
                        "서울특별시 광진구 능동로 120",
                        37.5412,
                        127.0784
                )
        );
    }

}

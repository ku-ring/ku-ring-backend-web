package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.dto.BuildingSummaryResult;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
import com.kustacks.kuring.building.application.port.out.dto.BuildingSummaryReadModel;
import com.kustacks.kuring.building.application.port.out.dto.CampusPlaceCategoryReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : CampusMapQueryService")
@ExtendWith(MockitoExtension.class)
class CampusMapQueryServiceTest {

    @Mock
    private CampusMapQueryPort campusMapQueryPort;

    @InjectMocks
    private CampusMapQueryService campusMapQueryService;

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
                        127.07382
                ),
                new BuildingSummaryReadModel(
                        2L,
                        "경영관",
                        "서울특별시 광진구 능동로 120",
                        37.54196,
                        127.07531
                )
        ));

        // when
        List<BuildingSummaryResult> result = campusMapQueryService.getBuildings();

        // then
        assertThat(result).containsExactly(
                new BuildingSummaryResult(
                        1L,
                        "행정관",
                        "서울특별시 광진구 능동로 120",
                        37.54241,
                        127.07382
                ),
                new BuildingSummaryResult(
                        2L,
                        "경영관",
                        "서울특별시 광진구 능동로 120",
                        37.54196,
                        127.07531
                )
        );
        verify(campusMapQueryPort).findBuildings();
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
                        127.0784
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
}

package com.kustacks.kuring.building.application.service;

import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.building.application.port.out.CampusMapQueryPort;
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
}

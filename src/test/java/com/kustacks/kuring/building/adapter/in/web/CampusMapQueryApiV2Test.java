package com.kustacks.kuring.building.adapter.in.web;

import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryDto;
import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.CategoryResult;
import com.kustacks.kuring.common.dto.BaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
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
        BaseResponse<?> body = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(body)
                .extracting(BaseResponse::getCode, BaseResponse::getMessage)
                .containsExactly(200, "장소 카테고리 목록 조회에 성공하였습니다");
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getData().categories())
                .extracting(
                        CategoryDto::name,
                        CategoryDto::korName,
                        CategoryDto::displayOrder
                )
                .containsExactly(
                        tuple("cafe", "카페", 1),
                        tuple("restaurant", "식당", 2)
                );
    }
}

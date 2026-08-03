package com.kustacks.kuring.building.adapter.in.web;

import com.kustacks.kuring.building.adapter.in.web.dto.CategoryListResponse;
import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_CATEGORY_LIST_SEARCH_SUCCESS;

@Tag(name = "Campus Map-Query", description = "캠퍼스맵 정보 조회")
@Validated
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "campus-map",
        name = "source",
        havingValue = "database"
)
@RestWebAdapter(path = "/api/v2/maps")
public class CampusMapQueryApiV2 {

    private final CampusMapQueryUseCase campusMapQueryUseCase;

    @Operation(summary = "캠퍼스맵 카테고리 목록 조회")
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<CategoryListResponse>> getCategories() {
        return ResponseEntity.ok(new BaseResponse<>(
                CAMPUS_MAP_CATEGORY_LIST_SEARCH_SUCCESS,
                CategoryListResponse.from(campusMapQueryUseCase.getCategories())
        ));
    }
}

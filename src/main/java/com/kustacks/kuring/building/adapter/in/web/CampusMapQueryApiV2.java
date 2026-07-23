package com.kustacks.kuring.building.adapter.in.web;

import com.kustacks.kuring.building.adapter.in.web.dto.BuildingDetailResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingSearchResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CampusPlaceListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CategoryListResponse;
import com.kustacks.kuring.building.application.port.in.CampusMapQueryUseCase;
import com.kustacks.kuring.building.application.port.in.dto.BuildingDetailResult;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.dto.ResponseCodeAndMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_BUILDING_DETAIL_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_BUILDING_LIST_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_BUILDING_NOT_FOUND;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_BUILDING_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_CATEGORY_LIST_SEARCH_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.CAMPUS_MAP_PLACE_LIST_SEARCH_SUCCESS;

@Tag(name = "Campus Map-Query", description = "캠퍼스맵 정보 조회")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/maps")
public class CampusMapQueryApiV2 {

    private final CampusMapQueryUseCase campusMapQueryUseCase;

    @Operation(summary = "캠퍼스맵 카테고리 목록 조회")
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<CategoryListResponse>> getCategories() {
        return ok(
                CAMPUS_MAP_CATEGORY_LIST_SEARCH_SUCCESS,
                CategoryListResponse.from(campusMapQueryUseCase.getCategories())
        );
    }

    @Operation(summary = "캠퍼스맵 전체 건물 목록 조회")
    @GetMapping("/buildings")
    public ResponseEntity<BaseResponse<BuildingListResponse>> getBuildings() {
        return ok(
                CAMPUS_MAP_BUILDING_LIST_SEARCH_SUCCESS,
                BuildingListResponse.from(campusMapQueryUseCase.getBuildings())
        );
    }

    @Operation(summary = "캠퍼스맵 건물 키워드 검색")
    @GetMapping("/buildings/search")
    public ResponseEntity<BaseResponse<BuildingSearchResponse>> searchBuildings(
            @Parameter(description = "건물명, 주소 또는 건물에 등록된 검색 키워드")
            @RequestParam(name = "keyword") @NotBlank String keyword
    ) {
        return ok(
                CAMPUS_MAP_BUILDING_SEARCH_SUCCESS,
                BuildingSearchResponse.from(campusMapQueryUseCase.searchBuildings(keyword))
        );
    }

    @Operation(summary = "캠퍼스맵 카테고리 기반 시설 목록 조회")
    @GetMapping("/campus-places")
    public ResponseEntity<BaseResponse<CampusPlaceListResponse>> getCampusPlaces(
            @Parameter(description = "쉼표로 구분된 시설 카테고리")
            @RequestParam(name = "categories") @NotEmpty List<String> categories
    ) {
        return ok(
                CAMPUS_MAP_PLACE_LIST_SEARCH_SUCCESS,
                CampusPlaceListResponse.from(campusMapQueryUseCase.getCampusPlaces(categories))
        );
    }

    @Operation(summary = "캠퍼스맵 건물 상세 조회")
    @GetMapping("/buildings/{buildingId}")
    public ResponseEntity<BaseResponse<BuildingDetailResponse>> getBuildingDetail(
            @PathVariable Long buildingId
    ) {
        Optional<BuildingDetailResult> result = campusMapQueryUseCase.getBuildingDetail(buildingId);

        if (result.isEmpty()) {
            return ResponseEntity.status(CAMPUS_MAP_BUILDING_NOT_FOUND.getCode())
                    .body(new BaseResponse<>(CAMPUS_MAP_BUILDING_NOT_FOUND, null));
        }

        BuildingDetailResponse response = BuildingDetailResponse.from(result.get());
        return ok(CAMPUS_MAP_BUILDING_DETAIL_SEARCH_SUCCESS, response);
    }

    private static <T> ResponseEntity<BaseResponse<T>> ok(
            ResponseCodeAndMessages response,
            T data
    ) {
        return ResponseEntity.ok(new BaseResponse<>(response, data));
    }
}

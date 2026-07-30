package com.kustacks.kuring.building.adapter.in.web;

import com.kustacks.kuring.building.adapter.in.web.dto.BuildingDetailResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingSearchResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CampusPlaceListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CategoryListResponse;
import com.kustacks.kuring.building.adapter.in.web.mock.CampusMapMockFixture;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Campus Map-Query", description = "캠퍼스맵 정보 조회")
@Validated
@ConditionalOnProperty(
        prefix = "campus-map",
        name = "source",
        havingValue = "mock",
        matchIfMissing = true
)
@RestWebAdapter(path = "/api/v2/maps")
public class CampusMapMockQueryApiV2 {

    // TODO: 실제 캠퍼스맵 데이터 등록 및 API 전환이 완료되면 제거한다.

    @Operation(summary = "캠퍼스맵 카테고리 목록 조회")
    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<CategoryListResponse>> getCategories() {
        return ok(
                "장소 카테고리 목록 조회에 성공하였습니다",
                CampusMapMockFixture.mockCategories()
        );
    }

    @Operation(summary = "캠퍼스맵 전체 건물 목록 조회")
    @GetMapping("/buildings")
    public ResponseEntity<BaseResponse<BuildingListResponse>> getBuildings() {
        return ok(
                "캠퍼스 건물 목록 조회에 성공하였습니다",
                CampusMapMockFixture.mockBuildings()
        );
    }

    @Operation(summary = "캠퍼스맵 건물 키워드 검색")
    @GetMapping("/buildings/search")
    public ResponseEntity<BaseResponse<BuildingSearchResponse>> searchBuildings(
            @Parameter(description = "건물명 또는 건물에 등록된 검색 키워드")
            @RequestParam(name = "keyword") @NotBlank String keyword
    ) {
        return ok(
                "캠퍼스 건물 검색에 성공하였습니다",
                CampusMapMockFixture.mockBuildingSearchResult(keyword)
        );
    }

    @Operation(summary = "캠퍼스맵 카테고리 기반 시설 목록 조회")
    @GetMapping("/campus-places")
    public ResponseEntity<BaseResponse<CampusPlaceListResponse>> getCampusPlaces(
            @Parameter(description = "쉼표로 구분된 시설 카테고리")
            @RequestParam(name = "categories") @NotEmpty List<@NotBlank String> categories
    ) {
        return ok(
                "카테고리 기반 시설 목록 조회에 성공하였습니다",
                CampusMapMockFixture.mockCampusPlaces(categories)
        );
    }

    @Operation(summary = "캠퍼스맵 건물 상세 조회")
    @GetMapping("/buildings/{buildingId}")
    public ResponseEntity<BaseResponse<BuildingDetailResponse>> getBuildingDetail(
            @PathVariable Long buildingId
    ) {
        var response = CampusMapMockFixture.findMockBuildingDetail(buildingId);

        return response.map(buildingDetailResponse
                -> ok("캠퍼스 건물 상세 조회에 성공하였습니다", buildingDetailResponse))
                .orElseGet(() -> ResponseEntity.status(404)
                .body(new BaseResponse<>(404, "캠퍼스 건물을 찾을 수 없습니다", null)));

    }

    private static <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new BaseResponse<>(200, message, data));
    }
}

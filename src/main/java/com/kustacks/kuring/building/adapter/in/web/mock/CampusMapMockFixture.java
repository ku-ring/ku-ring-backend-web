package com.kustacks.kuring.building.adapter.in.web.mock;

import com.kustacks.kuring.building.adapter.in.web.dto.BuildingDetailResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingSearchResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CampusPlaceListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CategoryListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceDetail;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceItem;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryItem;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CurrentOperatingHours;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class CampusMapMockFixture {

    private static final String ADDRESS = "서울특별시 광진구 능동로 120";

    private static final CurrentOperatingHours STUDENT_CENTER_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER,
                    OperatingDayGroup.WEEKDAY,
                    OperatingHoursStatus.SCHEDULED,
                    "08:00",
                    "22:00"
            );

    private static final CurrentOperatingHours BUSINESS_BUILDING_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER,
                    OperatingDayGroup.WEEKDAY,
                    OperatingHoursStatus.SCHEDULED,
                    "09:00",
                    "21:00"
            );

    private static final CurrentOperatingHours ATM_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER,
                    OperatingDayGroup.WEEKDAY,
                    OperatingHoursStatus.OPEN_24_HOURS,
                    null,
                    null
            );

    private static final BuildingSummary ADMINISTRATION_BUILDING =
            new BuildingSummary(1L, "행정관", ADDRESS, 37.54241, 127.07382);

    private static final BuildingSummary BUSINESS_BUILDING =
            new BuildingSummary(2L, "경영관", ADDRESS, 37.54196, 127.07531);

    private static final BuildingSummary LAW_BUILDING =
            new BuildingSummary(8L, "법학관", ADDRESS, 37.54174, 127.07649);

    private static final BuildingSummary STUDENT_CENTER_BUILDING =
            new BuildingSummary(20L, "학생회관", ADDRESS, 37.5412, 127.0784);

    private static final List<CategoryItem> CATEGORIES = List.of(
            new CategoryItem("cafe", "카페", 1),
            new CategoryItem("restaurant", "음식점", 2),
            new CategoryItem("printer", "프린터", 3),
            new CategoryItem("smoking_booth", "흡연부스", 4),
            new CategoryItem("convenience_store", "편의점", 5),
            new CategoryItem("lounge", "휴게공간", 6),
            new CategoryItem("kcube", "K-Cube", 7)
    );

    private static final List<BuildingSummary> BUILDINGS = List.of(
            ADMINISTRATION_BUILDING,
            BUSINESS_BUILDING,
            LAW_BUILDING,
            STUDENT_CENTER_BUILDING
    );

    private static final List<BuildingSummary> SEARCHABLE_BUILDINGS = List.of(
            ADMINISTRATION_BUILDING,
            BUSINESS_BUILDING,
            new BuildingSummary(6L, "상허기념도서관", ADDRESS, 37.5423, 127.0731),
            new BuildingSummary(7L, "상허연구관", ADDRESS, 37.54304, 127.07421),
            LAW_BUILDING,
            STUDENT_CENTER_BUILDING
    );

    private static final Map<Long, List<String>> BUILDING_SEARCH_KEYWORDS = Map.of(
            8L, List.of("종강")
    );

    private static final List<CampusPlaceItem> CAMPUS_PLACES = List.of(
            new CampusPlaceItem(
                    101L,
                    "학생회관 편의점",
                    "convenience_store",
                    "편의점",
                    "https://cdn.example.com/campus-place/101.jpg",
                    CampusPlaceLocationType.INDOOR,
                    "1F",
                    "학생회관 정문 오른쪽",
                    null,
                    STUDENT_CENTER_HOURS,
                    null,
                    STUDENT_CENTER_BUILDING
            ),
            new CampusPlaceItem(
                    102L,
                    "학생회관 1층 라운지 프린터",
                    "printer",
                    "프린터",
                    "https://cdn.example.com/campus-place/102.jpg",
                    CampusPlaceLocationType.INDOOR,
                    "1F",
                    "라운지 안쪽",
                    3,
                    STUDENT_CENTER_HOURS,
                    null,
                    STUDENT_CENTER_BUILDING
            ),
            new CampusPlaceItem(
                    203L,
                    "경영관 지하 1층 프린터",
                    "printer",
                    "프린터",
                    "https://cdn.example.com/campus-place/203.jpg",
                    CampusPlaceLocationType.INDOOR,
                    "B1",
                    "엘리베이터 맞은편",
                    1,
                    BUSINESS_BUILDING_HOURS,
                    null,
                    BUSINESS_BUILDING
            )
    );

    private static final BuildingDetailResponse STUDENT_CENTER_DETAIL =
            new BuildingDetailResponse(
                    20L,
                    "학생회관",
                    ADDRESS,
                    37.5412,
                    127.0784,
                    "https://cdn.example.com/building/20.jpg",
                    STUDENT_CENTER_HOURS,
                    List.of(
                            new CampusPlaceDetail(
                                    101L,
                                    "학생회관 편의점",
                                    "convenience_store",
                                    "편의점",
                                    "https://cdn.example.com/campus-place/101.jpg",
                                    CampusPlaceLocationType.INDOOR,
                                    "1F",
                                    "정문 오른쪽",
                                    null,
                                    STUDENT_CENTER_HOURS,
                                    null
                            ),
                            new CampusPlaceDetail(
                                    102L,
                                    "학생회관 1층 라운지 프린터",
                                    "printer",
                                    "프린터",
                                    "https://cdn.example.com/campus-place/102.jpg",
                                    CampusPlaceLocationType.INDOOR,
                                    "1F",
                                    "라운지 안쪽",
                                    3,
                                    STUDENT_CENTER_HOURS,
                                    null
                            ),
                            new CampusPlaceDetail(
                                    103L,
                                    "신한은행 학생회관 ATM",
                                    "bank_atm",
                                    "은행·ATM",
                                    "https://cdn.example.com/campus-place/103.jpg",
                                    CampusPlaceLocationType.INDOOR,
                                    "1F",
                                    "서측 출입구 옆",
                                    2,
                                    ATM_HOURS,
                                    null
                            )
                    )
            );

    private static final Map<Long, BuildingDetailResponse> BUILDING_DETAILS = Map.of(
            STUDENT_CENTER_DETAIL.id(), STUDENT_CENTER_DETAIL
    );

    private CampusMapMockFixture() {
    }

    public static CategoryListResponse categories() {
        return new CategoryListResponse(CATEGORIES);
    }

    public static BuildingListResponse buildings() {
        return new BuildingListResponse(BUILDINGS);
    }

    public static BuildingSearchResponse searchBuildings(String keyword) {
        String normalizedKeyword = normalize(keyword);

        var buildings = SEARCHABLE_BUILDINGS.stream()
                .filter(building -> matchesKeyword(building, normalizedKeyword))
                .toList();

        return new BuildingSearchResponse(buildings);
    }

    public static CampusPlaceListResponse campusPlaces(List<String> categories) {
        Set<String> requestedCategories = categories.stream()
                .flatMap(category -> Arrays.stream(category.split(",")))
                .map(CampusMapMockFixture::normalize)
                .filter(category -> !category.isBlank())
                .collect(Collectors.toSet());

        var campusPlaces = CAMPUS_PLACES.stream()
                .filter(place -> requestedCategories.contains(place.category()))
                .toList();

        return new CampusPlaceListResponse(campusPlaces);
    }

    public static Optional<BuildingDetailResponse> findBuildingDetail(Long buildingId) {
        return Optional.ofNullable(BUILDING_DETAILS.get(buildingId));
    }

    private static boolean matchesKeyword(
            BuildingSummary building,
            String normalizedKeyword
    ) {
        if (normalize(building.name()).contains(normalizedKeyword)) {
            return true;
        }

        return BUILDING_SEARCH_KEYWORDS.getOrDefault(building.id(), List.of()).stream()
                .map(CampusMapMockFixture::normalize)
                .anyMatch(keyword -> keyword.contains(normalizedKeyword));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}

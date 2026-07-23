package com.kustacks.kuring.building.adapter.in.web.mock;

import com.kustacks.kuring.building.adapter.in.web.dto.BuildingDetailResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.BuildingSearchResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CampusPlaceListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.CategoryListResponse;
import com.kustacks.kuring.building.adapter.in.web.dto.model.BuildingSummary;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceDetail;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CampusPlaceItem;
import com.kustacks.kuring.building.adapter.in.web.dto.model.CategoryDto;
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
import java.util.stream.Stream;

//TODO : 실제 데이터 기반 API 구현 완료 시 제거
public final class CampusMapMockFixture {

    private CampusMapMockFixture() {
    }

    private static final String ADDRESS = "서울특별시 광진구 능동로 120";

    private static final CurrentOperatingHours STUDENT_CENTER_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "08:00",
                    "22:00"
            );

    private static final CurrentOperatingHours BUSINESS_BUILDING_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "09:00",
                    "21:00"
            );

    private static final CurrentOperatingHours OPEN_24_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.OPEN_24_HOURS.toString(),
                    null,
                    null
            );

    private static final CurrentOperatingHours RESTAURANT_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "10:30",
                    "19:00"
            );

    private static final CurrentOperatingHours UNKNOWN_HOURS =
            new CurrentOperatingHours(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.UNKNOWN.toString(),
                    null,
                    null
            );

    private static final BuildingSummary ADMINISTRATION_BUILDING =
            new BuildingSummary(1L, "행정관", ADDRESS, 37.54241, 127.07382);

    private static final BuildingSummary BUSINESS_BUILDING =
            new BuildingSummary(2L, "경영관", ADDRESS, 37.54196, 127.07531);

    private static final BuildingSummary LAW_BUILDING =
            new BuildingSummary(3L, "법학관", ADDRESS, 37.54174, 127.07649);

    private static final BuildingSummary STUDENT_CENTER_BUILDING =
            new BuildingSummary(4L, "학생회관", ADDRESS, 37.5412, 127.0784);

    private static final List<CategoryDto> CATEGORIES = List.of(
            new CategoryDto("cafe", "카페", 1),
            new CategoryDto("restaurant", "음식점", 2),
            new CategoryDto("printer", "프린터", 3),
            new CategoryDto("smoking_booth", "흡연부스", 4),
            new CategoryDto("convenience_store", "편의점", 5),
            new CategoryDto("lounge", "휴게공간", 6),
            new CategoryDto("kcube", "K-Cube", 7)
    );

    private static final List<BuildingSummary> BUILDINGS = List.of(
            ADMINISTRATION_BUILDING,
            BUSINESS_BUILDING,
            LAW_BUILDING,
            STUDENT_CENTER_BUILDING
    );

    private static final List<BuildingSummary> SEARCHABLE_BUILDINGS = BUILDINGS;

    private static final Map<Long, List<String>> BUILDING_SEARCH_KEYWORDS = Map.of(
            3L, List.of("종강"),
            4L, List.of("학관")
    );

    private static final List<CampusPlaceItem> CAMPUS_PLACES = List.of(
            new CampusPlaceItem(
                    101L,
                    "학생회관 편의점",
                    "convenience_store",
                    "편의점",
                    "https://placehold.co/600x400/png?text=Convenience+Store",
                    CampusPlaceLocationType.INDOOR.toString(),
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
                    "https://placehold.co/600x400/png?text=Printer",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "1F",
                    "라운지 안쪽",
                    3,
                    STUDENT_CENTER_HOURS,
                    null,
                    STUDENT_CENTER_BUILDING
            ),
            new CampusPlaceItem(
                    104L,
                    "학생회관 학생식당",
                    "restaurant",
                    "음식점",
                    "https://placehold.co/600x400/png?text=Restaurant",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "B1",
                    "지하 1층 중앙",
                    null,
                    RESTAURANT_HOURS,
                    null,
                    STUDENT_CENTER_BUILDING
            ),
            new CampusPlaceItem(
                    201L,
                    "경영관 카페",
                    "cafe",
                    "카페",
                    "https://placehold.co/600x400/png?text=Cafe",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "1F",
                    "정문 왼쪽",
                    null,
                    BUSINESS_BUILDING_HOURS,
                    null,
                    BUSINESS_BUILDING
            ),
            new CampusPlaceItem(
                    202L,
                    "경영관 2층 휴게공간",
                    "lounge",
                    "휴게공간",
                    "https://placehold.co/600x400/png?text=Lounge",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "2F",
                    "중앙 계단 옆",
                    null,
                    UNKNOWN_HOURS,
                    null,
                    BUSINESS_BUILDING
            ),
            new CampusPlaceItem(
                    203L,
                    "경영관 지하 1층 프린터",
                    "printer",
                    "프린터",
                    "https://placehold.co/600x400/png?text=Printer",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "B1",
                    "엘리베이터 맞은편",
                    1,
                    BUSINESS_BUILDING_HOURS,
                    null,
                    BUSINESS_BUILDING
            ),
            new CampusPlaceItem(
                    301L,
                    "법학관 K-Cube",
                    "kcube",
                    "K-Cube",
                    "https://placehold.co/600x400/png?text=K-Cube",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "1F",
                    "1층 로비",
                    null,
                    BUSINESS_BUILDING_HOURS,
                    "https://wein.konkuk.ac.kr/",
                    LAW_BUILDING
            ),
            new CampusPlaceItem(
                    401L,
                    "행정관 외부 흡연부스",
                    "smoking_booth",
                    "흡연부스",
                    "https://placehold.co/600x400/png?text=Smoking+Booth",
                    CampusPlaceLocationType.OUTDOOR.toString(),
                    "OUTDOOR",
                    "행정관 후문 맞은편",
                    1,
                    OPEN_24_HOURS,
                    null,
                    ADMINISTRATION_BUILDING
            )
    );

    private static final CampusPlaceDetail STUDENT_CENTER_ATM =
            new CampusPlaceDetail(
                    103L,
                    "신한은행 학생회관 ATM",
                    "bank_atm",
                    "은행·ATM",
                    "https://placehold.co/600x400/png?text=ATM",
                    CampusPlaceLocationType.INDOOR.toString(),
                    "1F",
                    "서측 출입구 옆",
                    2,
                    OPEN_24_HOURS,
                    null
            );

    private static final BuildingDetailResponse ADMINISTRATION_BUILDING_DETAIL = buildingDetail(
            ADMINISTRATION_BUILDING,
            "https://placehold.co/1200x800/png?text=Administration+Building",
            BUSINESS_BUILDING_HOURS
    );

    private static final BuildingDetailResponse BUSINESS_BUILDING_DETAIL = buildingDetail(
            BUSINESS_BUILDING,
            "https://placehold.co/1200x800/png?text=Business+Building",
            BUSINESS_BUILDING_HOURS
    );

    private static final BuildingDetailResponse LAW_BUILDING_DETAIL = buildingDetail(
            LAW_BUILDING,
            "https://placehold.co/1200x800/png?text=Law+Building",
            BUSINESS_BUILDING_HOURS
    );

    private static final BuildingDetailResponse STUDENT_CENTER_DETAIL = buildingDetail(
            STUDENT_CENTER_BUILDING,
            "https://placehold.co/1200x800/png?text=Student+Center",
            STUDENT_CENTER_HOURS,
            STUDENT_CENTER_ATM
    );

    private static final Map<Long, BuildingDetailResponse> BUILDING_DETAILS = Map.of(
            ADMINISTRATION_BUILDING_DETAIL.id(), ADMINISTRATION_BUILDING_DETAIL,
            BUSINESS_BUILDING_DETAIL.id(), BUSINESS_BUILDING_DETAIL,
            LAW_BUILDING_DETAIL.id(), LAW_BUILDING_DETAIL,
            STUDENT_CENTER_DETAIL.id(), STUDENT_CENTER_DETAIL
    );

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

    private static BuildingDetailResponse buildingDetail(
            BuildingSummary building,
            String imageUrl,
            CurrentOperatingHours currentOperatingHours,
            CampusPlaceDetail... additionalCampusPlaces
    ) {
        var campusPlaces = Stream.concat(
                CAMPUS_PLACES.stream()
                        .filter(campusPlace -> campusPlace.building().id().equals(building.id()))
                        .map(CampusMapMockFixture::campusPlaceDetail),
                Arrays.stream(additionalCampusPlaces)
        ).toList();

        return new BuildingDetailResponse(
                building.id(),
                building.name(),
                building.address(),
                building.latitude(),
                building.longitude(),
                imageUrl,
                currentOperatingHours,
                campusPlaces
        );
    }

    private static CampusPlaceDetail campusPlaceDetail(CampusPlaceItem campusPlace) {
        return new CampusPlaceDetail(
                campusPlace.id(),
                campusPlace.name(),
                campusPlace.category(),
                campusPlace.categoryKorName(),
                campusPlace.imageUrl(),
                campusPlace.locationType(),
                campusPlace.floor(),
                campusPlace.locationDetail(),
                campusPlace.quantity(),
                campusPlace.currentOperatingHours(),
                campusPlace.externalUrl()
        );
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

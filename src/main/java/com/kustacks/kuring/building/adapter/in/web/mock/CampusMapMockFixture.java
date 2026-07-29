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
import com.kustacks.kuring.building.adapter.in.web.dto.model.OperatingHoursDto;
import com.kustacks.kuring.building.domain.CampusPlaceLocationType;
import com.kustacks.kuring.building.domain.OperatingDayGroup;
import com.kustacks.kuring.building.domain.OperatingHoursStatus;
import com.kustacks.kuring.building.domain.OperatingPeriod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO : 실제 데이터 기반 API 구현 완료 시 제거
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CampusMapMockFixture {

    private static final String ADDRESS = "서울특별시 광진구 능동로 120";
    private static final String KCUBE_EXTERNAL_URL = "https://wein.konkuk.ac.kr/";

    private static final String MOCK_BUILDING_IMAGE_URL = "https://placehold.co/1200x800/png?text=Konkuk+University";
    private static final String MOCK_CAFE_IMAGE_URL = "https://placehold.co/600x400/png?text=Cafe";
    private static final String MOCK_RESTAURANT_IMAGE_URL = "https://placehold.co/600x400/png?text=Restaurant";
    private static final String MOCK_PRINTER_IMAGE_URL = "https://placehold.co/600x400/png?text=Printer";
    private static final String MOCK_CONVENIENCE_STORE_IMAGE_URL = "https://placehold.co/600x400/png?text=Convenience+Store";
    private static final String MOCK_LOUNGE_IMAGE_URL = "https://placehold.co/600x400/png?text=Lounge";
    private static final String MOCK_KCUBE_IMAGE_URL = "https://placehold.co/600x400/png?text=K-Cube";

    private static final List<OperatingHoursDto> MOCK_BUILDING_HOURS = List.of(
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "08:00",
                    "22:00",
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "09:00",
                    "18:00",
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "09:00",
                    "18:00",
                    true
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.UNKNOWN.toString(),
                    null,
                    null,
                    false
            )
    );

    private static final List<OperatingHoursDto> MOCK_STORE_HOURS = List.of(
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "08:00",
                    "22:00",
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "09:00",
                    "18:00",
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "09:00",
                    "18:00",
                    true
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.UNKNOWN.toString(),
                    null,
                    null,
                    false
            )
    );

    private static final List<OperatingHoursDto> MOCK_RESTAURANT_HOURS = List.of(
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "10:30",
                    "19:00",
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.UNKNOWN.toString(),
                    null,
                    null,
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.SCHEDULED.toString(),
                    "11:00",
                    "17:00",
                    true
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.UNKNOWN.toString(),
                    null,
                    null,
                    false
            )
    );

    private static final List<OperatingHoursDto> MOCK_OPEN_24_HOURS = List.of(
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.OPEN_24_HOURS.toString(),
                    null,
                    null,
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.SEMESTER.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.OPEN_24_HOURS.toString(),
                    null,
                    null,
                    false
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKDAY.toString(),
                    OperatingHoursStatus.OPEN_24_HOURS.toString(),
                    null,
                    null,
                    true
            ),
            new OperatingHoursDto(
                    OperatingPeriod.VACATION.toString(),
                    OperatingDayGroup.WEEKEND.toString(),
                    OperatingHoursStatus.OPEN_24_HOURS.toString(),
                    null,
                    null,
                    false
            )
    );

    private static final BuildingSummary ADMINISTRATION_BUILDING =
            new BuildingSummary(1L, "행정관", ADDRESS, 37.5444801, 127.0748518);
    private static final BuildingSummary BUSINESS_BUILDING =
            new BuildingSummary(2L, "경영관", ADDRESS, 37.5443474, 127.0761119);
    private static final BuildingSummary LAW_BUILDING =
            new BuildingSummary(3L, "법학관", ADDRESS, 37.5417036, 127.0750767);
    private static final BuildingSummary STUDENT_CENTER_BUILDING =
            new BuildingSummary(4L, "학생회관", ADDRESS, 37.5418230, 127.0779007);
    private static final BuildingSummary SANGHUH_RESEARCH_BUILDING =
            new BuildingSummary(5L, "상허연구관", ADDRESS, 37.5441512, 127.0752711);
    private static final BuildingSummary EDUCATION_SCIENCE_BUILDING =
            new BuildingSummary(6L, "교육과학관", ADDRESS, 37.5440445, 127.0742795);
    private static final BuildingSummary ARTS_AND_DESIGN_BUILDING =
            new BuildingSummary(7L, "예술문화관", ADDRESS, 37.5428728, 127.0731471);
    private static final BuildingSummary LANGUAGE_INSTITUTE_BUILDING =
            new BuildingSummary(8L, "언어교육원", ADDRESS, 37.5425576, 127.0747825);
    private static final BuildingSummary MUSEUM_BUILDING =
            new BuildingSummary(9L, "박물관", ADDRESS, 37.5423644, 127.0756128);
    private static final BuildingSummary SANGHUH_LIBRARY_BUILDING =
            new BuildingSummary(10L, "상허기념도서관", ADDRESS, 37.5420474, 127.0738384);
    private static final BuildingSummary BIOMEDICAL_SCIENCE_BUILDING =
            new BuildingSummary(11L, "의생명과학연구관", ADDRESS, 37.5414668, 127.0723294);
    private static final BuildingSummary LIFE_SCIENCES_BUILDING =
            new BuildingSummary(12L, "생명과학관", ADDRESS, 37.5408856, 127.0742996);
    private static final BuildingSummary ANIMAL_SCIENCES_BUILDING =
            new BuildingSummary(13L, "동물생명과학관", ADDRESS, 37.5403172, 127.0743016);
    private static final BuildingSummary ADMISSION_INFORMATION_BUILDING =
            new BuildingSummary(14L, "입학정보관", ADDRESS, 37.5402602, 127.0735777);
    private static final BuildingSummary INDUSTRY_COOPERATION_BUILDING =
            new BuildingSummary(15L, "산학협동관", ADDRESS, 37.5397277, 127.0732197);
    private static final BuildingSummary NEW_MILLENNIUM_BUILDING =
            new BuildingSummary(16L, "새천년관", ADDRESS, 37.5435675, 127.0774647);
    private static final BuildingSummary ARCHITECTURE_BUILDING =
            new BuildingSummary(17L, "건축관", ADDRESS, 37.5434694, 127.0784422);
    private static final BuildingSummary REAL_ESTATE_BUILDING =
            new BuildingSummary(18L, "해봉부동산학관", ADDRESS, 37.5433334, 127.0782018);
    private static final BuildingSummary LIBERAL_ARTS_BUILDING =
            new BuildingSummary(19L, "인문학관", ADDRESS, 37.5427150, 127.0787664);
    private static final BuildingSummary SCIENCE_BUILDING =
            new BuildingSummary(20L, "과학관", ADDRESS, 37.5414670, 127.0805824);

    private static final List<CategoryDto> CATEGORIES = List.of(
            new CategoryDto("cafe", "카페", 1),
            new CategoryDto("restaurant", "식당", 2),
            new CategoryDto("printer", "프린터", 3),
            new CategoryDto("smoking_booth", "흡연부스", 4),
            new CategoryDto("convenience_store", "편의점", 5),
            new CategoryDto("lounge", "휴게실", 6),
            new CategoryDto("kcube", "KCUBE", 7)
    );

    private static final List<BuildingSummary> BUILDINGS = List.of(
            ADMINISTRATION_BUILDING,
            BUSINESS_BUILDING,
            LAW_BUILDING,
            STUDENT_CENTER_BUILDING,
            SANGHUH_RESEARCH_BUILDING,
            EDUCATION_SCIENCE_BUILDING,
            ARTS_AND_DESIGN_BUILDING,
            LANGUAGE_INSTITUTE_BUILDING,
            MUSEUM_BUILDING,
            SANGHUH_LIBRARY_BUILDING,
            BIOMEDICAL_SCIENCE_BUILDING,
            LIFE_SCIENCES_BUILDING,
            ANIMAL_SCIENCES_BUILDING,
            ADMISSION_INFORMATION_BUILDING,
            INDUSTRY_COOPERATION_BUILDING,
            NEW_MILLENNIUM_BUILDING,
            ARCHITECTURE_BUILDING,
            REAL_ESTATE_BUILDING,
            LIBERAL_ARTS_BUILDING,
            SCIENCE_BUILDING
    );

    private static final Map<Long, List<String>> BUILDING_SEARCH_KEYWORDS = Map.of(
            LAW_BUILDING.id(), List.of("종강", "종합강의동"),
            STUDENT_CENTER_BUILDING.id(), List.of("학관", "제1학생회관")
    );

    private static final List<CampusPlaceItem> CAMPUS_PLACES = List.of(
            cafe(201L, "카페 레스티오", "1F", "경영관 1층", BUSINESS_BUILDING),
            convenienceStore(202L, "CU 경영관점", "1F", "경영관 1층", BUSINESS_BUILDING),
            kcube(203L, "경영관 K-Hub", "1F", "경영관 1층", BUSINESS_BUILDING),
            lounge(204L, "경영관 휴게실", null, "경영관 내부", BUSINESS_BUILDING),

            printer(301L, "법학관 인쇄소", "B1", "법학관 지하 1층", LAW_BUILDING),
            lounge(302L, "법학관 휴게실", null, "법학관 내부", LAW_BUILDING),

            convenienceStore(401L, "CU 학생회관점", "1F", "학생회관 1층", STUDENT_CENTER_BUILDING),
            printer(402L, "학생회관 복사실", "1F", "학생회관 1층", STUDENT_CENTER_BUILDING),
            restaurant(403L, "KU's Kitchen", "1F", "학생회관 1층", STUDENT_CENTER_BUILDING),
            restaurant(404L, "구시아 푸드마켓", "B1", "학생회관 지하 1층", STUDENT_CENTER_BUILDING),
            cafe(405L, "1847 샐러드카페", "1F", "학생회관 1층", STUDENT_CENTER_BUILDING),

            printer(501L, "상허연구관 복사실", "1F", "상허연구관 1층", SANGHUH_RESEARCH_BUILDING),
            cafe(502L, "카페 블루포트", "1F", "상허연구관 1층", SANGHUH_RESEARCH_BUILDING),
            kcube(503L, "상허연구관 K-Cube", "3F", "상허연구관 3층", SANGHUH_RESEARCH_BUILDING),

            cafe(701L, "카페 레스티오", "B1", "예술문화관 지하 1층", ARTS_AND_DESIGN_BUILDING),

            convenienceStore(1001L, "CU 상허기념도서관점", "3F", "상허기념도서관 3층", SANGHUH_LIBRARY_BUILDING),
            restaurant(1002L, "구시아 푸드마켓", "B1", "상허기념도서관 지하 1층", SANGHUH_LIBRARY_BUILDING),
            kcube(1003L, "상허기념도서관 K-Cube", "6F", "상허기념도서관 6층", SANGHUH_LIBRARY_BUILDING),

            convenienceStore(1101L, "CU 의생명과학연구관점", "1F", "의생명과학연구관 1층", BIOMEDICAL_SCIENCE_BUILDING),

            kcube(1201L, "생명과학관 K-Cube", "3F", "생명과학관 3층", LIFE_SCIENCES_BUILDING),

            cafe(1301L, "카페 레스티오", "1F", "동물생명과학관 1층", ANIMAL_SCIENCES_BUILDING),
            kcube(1302L, "동물생명과학관 K-Cube", "1F", "동물생명과학관 1층", ANIMAL_SCIENCES_BUILDING),

            convenienceStore(1501L, "이마트24 산학협동관점", "1F", "산학협동관 1층", INDUSTRY_COOPERATION_BUILDING),
            printer(1502L, "산학협동관 복사실", "2F", "산학협동관 2층", INDUSTRY_COOPERATION_BUILDING),

            restaurant(1601L, "KU's Dining", "B1", "새천년관 지하 1층", NEW_MILLENNIUM_BUILDING),

            kcube(1701L, "건축관 K-Hub", "1F", "건축관 1층", ARCHITECTURE_BUILDING),

            cafe(1801L, "카페 ING", "1F", "해봉부동산학관 1층", REAL_ESTATE_BUILDING),

            kcube(2001L, "과학관 K-Hub", "1F", "과학관 1층", SCIENCE_BUILDING)
    );

    private static final CampusPlaceDetail STUDENT_CENTER_BANK = new CampusPlaceDetail(
            406L,
            "신한은행",
            "bank_atm",
            "은행·ATM",
            "https://placehold.co/600x400/png?text=Shinhan+Bank",
            CampusPlaceLocationType.INDOOR.toString(),
            "1F",
            "학생회관 1층",
            null,
            MOCK_STORE_HOURS,
            null
    );

    private static final CampusPlaceDetail STUDENT_CENTER_POST_OFFICE = new CampusPlaceDetail(
            407L,
            "건국대학교 우편취급국",
            "post_office",
            "우편",
            "https://placehold.co/600x400/png?text=Post+Office",
            CampusPlaceLocationType.INDOOR.toString(),
            "1F",
            "학생회관 1층",
            null,
            MOCK_STORE_HOURS,
            null
    );

    private static final BuildingDetailResponse ADMINISTRATION_BUILDING_DETAIL =
            buildingDetail(ADMINISTRATION_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse BUSINESS_BUILDING_DETAIL =
            buildingDetail(BUSINESS_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse LAW_BUILDING_DETAIL =
            buildingDetail(LAW_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse STUDENT_CENTER_DETAIL = buildingDetail(
            STUDENT_CENTER_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS, STUDENT_CENTER_BANK, STUDENT_CENTER_POST_OFFICE);
    private static final BuildingDetailResponse SANGHUH_RESEARCH_BUILDING_DETAIL =
            buildingDetail(SANGHUH_RESEARCH_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse EDUCATION_SCIENCE_BUILDING_DETAIL =
            buildingDetail(EDUCATION_SCIENCE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse ARTS_AND_DESIGN_BUILDING_DETAIL =
            buildingDetail(ARTS_AND_DESIGN_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse LANGUAGE_INSTITUTE_BUILDING_DETAIL =
            buildingDetail(LANGUAGE_INSTITUTE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse MUSEUM_BUILDING_DETAIL =
            buildingDetail(MUSEUM_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse SANGHUH_LIBRARY_BUILDING_DETAIL =
            buildingDetail(SANGHUH_LIBRARY_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse BIOMEDICAL_SCIENCE_BUILDING_DETAIL =
            buildingDetail(BIOMEDICAL_SCIENCE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse LIFE_SCIENCES_BUILDING_DETAIL =
            buildingDetail(LIFE_SCIENCES_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse ANIMAL_SCIENCES_BUILDING_DETAIL =
            buildingDetail(ANIMAL_SCIENCES_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse ADMISSION_INFORMATION_BUILDING_DETAIL =
            buildingDetail(ADMISSION_INFORMATION_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse INDUSTRY_COOPERATION_BUILDING_DETAIL =
            buildingDetail(INDUSTRY_COOPERATION_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse NEW_MILLENNIUM_BUILDING_DETAIL =
            buildingDetail(NEW_MILLENNIUM_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse ARCHITECTURE_BUILDING_DETAIL =
            buildingDetail(ARCHITECTURE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse REAL_ESTATE_BUILDING_DETAIL =
            buildingDetail(REAL_ESTATE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse LIBERAL_ARTS_BUILDING_DETAIL =
            buildingDetail(LIBERAL_ARTS_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);
    private static final BuildingDetailResponse SCIENCE_BUILDING_DETAIL =
            buildingDetail(SCIENCE_BUILDING, MOCK_BUILDING_IMAGE_URL, MOCK_BUILDING_HOURS);

    private static final Map<Long, BuildingDetailResponse> BUILDING_DETAILS = Map.ofEntries(
            Map.entry(ADMINISTRATION_BUILDING_DETAIL.id(), ADMINISTRATION_BUILDING_DETAIL),
            Map.entry(BUSINESS_BUILDING_DETAIL.id(), BUSINESS_BUILDING_DETAIL),
            Map.entry(LAW_BUILDING_DETAIL.id(), LAW_BUILDING_DETAIL),
            Map.entry(STUDENT_CENTER_DETAIL.id(), STUDENT_CENTER_DETAIL),
            Map.entry(SANGHUH_RESEARCH_BUILDING_DETAIL.id(), SANGHUH_RESEARCH_BUILDING_DETAIL),
            Map.entry(EDUCATION_SCIENCE_BUILDING_DETAIL.id(), EDUCATION_SCIENCE_BUILDING_DETAIL),
            Map.entry(ARTS_AND_DESIGN_BUILDING_DETAIL.id(), ARTS_AND_DESIGN_BUILDING_DETAIL),
            Map.entry(LANGUAGE_INSTITUTE_BUILDING_DETAIL.id(), LANGUAGE_INSTITUTE_BUILDING_DETAIL),
            Map.entry(MUSEUM_BUILDING_DETAIL.id(), MUSEUM_BUILDING_DETAIL),
            Map.entry(SANGHUH_LIBRARY_BUILDING_DETAIL.id(), SANGHUH_LIBRARY_BUILDING_DETAIL),
            Map.entry(BIOMEDICAL_SCIENCE_BUILDING_DETAIL.id(), BIOMEDICAL_SCIENCE_BUILDING_DETAIL),
            Map.entry(LIFE_SCIENCES_BUILDING_DETAIL.id(), LIFE_SCIENCES_BUILDING_DETAIL),
            Map.entry(ANIMAL_SCIENCES_BUILDING_DETAIL.id(), ANIMAL_SCIENCES_BUILDING_DETAIL),
            Map.entry(ADMISSION_INFORMATION_BUILDING_DETAIL.id(), ADMISSION_INFORMATION_BUILDING_DETAIL),
            Map.entry(INDUSTRY_COOPERATION_BUILDING_DETAIL.id(), INDUSTRY_COOPERATION_BUILDING_DETAIL),
            Map.entry(NEW_MILLENNIUM_BUILDING_DETAIL.id(), NEW_MILLENNIUM_BUILDING_DETAIL),
            Map.entry(ARCHITECTURE_BUILDING_DETAIL.id(), ARCHITECTURE_BUILDING_DETAIL),
            Map.entry(REAL_ESTATE_BUILDING_DETAIL.id(), REAL_ESTATE_BUILDING_DETAIL),
            Map.entry(LIBERAL_ARTS_BUILDING_DETAIL.id(), LIBERAL_ARTS_BUILDING_DETAIL),
            Map.entry(SCIENCE_BUILDING_DETAIL.id(), SCIENCE_BUILDING_DETAIL)
    );

    public static CategoryListResponse mockCategories() {
        return new CategoryListResponse(CATEGORIES);
    }

    public static BuildingListResponse mockBuildings() {
        return new BuildingListResponse(BUILDINGS);
    }

    public static BuildingSearchResponse mockBuildingSearchResult(String keyword) {
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        var buildings = BUILDINGS.stream()
                .filter(building -> containsKeyword(building, normalizedKeyword))
                .toList();

        return new BuildingSearchResponse(buildings);
    }

    public static CampusPlaceListResponse mockCampusPlaces(List<String> categories) {
        Set<String> normalizedCategories = categories.stream()
                .map(category -> category.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        var campusPlaces = CAMPUS_PLACES.stream()
                .filter(campusPlace -> normalizedCategories.contains(campusPlace.category()))
                .toList();

        return new CampusPlaceListResponse(campusPlaces);
    }

    public static Optional<BuildingDetailResponse> findMockBuildingDetail(Long buildingId) {
        return Optional.ofNullable(BUILDING_DETAILS.get(buildingId));
    }

    private static boolean containsKeyword(BuildingSummary building, String keyword) {
        if (building.name().toLowerCase(Locale.ROOT).contains(keyword)
                || building.address().toLowerCase(Locale.ROOT).contains(keyword)) {
            return true;
        }

        return BUILDING_SEARCH_KEYWORDS.getOrDefault(building.id(), List.of()).stream()
                .anyMatch(searchKeyword -> searchKeyword.toLowerCase(Locale.ROOT).contains(keyword));
    }

    private static CampusPlaceItem cafe(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "cafe",
                "카페",
                MOCK_CAFE_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_STORE_HOURS,
                null,
                building
        );
    }

    private static CampusPlaceItem restaurant(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "restaurant",
                "식당",
                MOCK_RESTAURANT_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_RESTAURANT_HOURS,
                null,
                building
        );
    }

    private static CampusPlaceItem printer(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "printer",
                "프린터",
                MOCK_PRINTER_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_STORE_HOURS,
                null,
                building
        );
    }

    private static CampusPlaceItem convenienceStore(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "convenience_store",
                "편의점",
                MOCK_CONVENIENCE_STORE_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_STORE_HOURS,
                null,
                building
        );
    }

    private static CampusPlaceItem lounge(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "lounge",
                "휴게실",
                MOCK_LOUNGE_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_OPEN_24_HOURS,
                null,
                building
        );
    }

    private static CampusPlaceItem kcube(
            Long id,
            String name,
            String floor,
            String locationDetail,
            BuildingSummary building
    ) {
        return new CampusPlaceItem(
                id,
                name,
                "kcube",
                "KCUBE",
                MOCK_KCUBE_IMAGE_URL,
                CampusPlaceLocationType.INDOOR.toString(),
                floor,
                locationDetail,
                null,
                MOCK_BUILDING_HOURS,
                KCUBE_EXTERNAL_URL,
                building
        );
    }

    private static BuildingDetailResponse buildingDetail(
            BuildingSummary building,
            String imageUrl,
            List<OperatingHoursDto> operatingHours,
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
                operatingHours,
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
                campusPlace.operatingHours(),
                campusPlace.externalUrl()
        );
    }
}

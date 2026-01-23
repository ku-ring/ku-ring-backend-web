package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kustacks.kuring.common.exception.code.ErrorCode.DEPARTMENT_NOT_FOUND;

@Getter
public enum DepartmentName {

    KOREAN("korean", "korea", null, "국어국문학과"),
    ENGLISH("english", "english", null, "영어영문학과"),
    CHINESE("chinese", "china", null, "중어중문학과"),
    PHILOSOPHY("philosophy", "philo", null, "철학과"),
    HISTORY("khistory", "khistory", null, "사학과"),
    GEOLOGY("geology", "kugeo", null, "지리학과"),
    MEDIA_COMM("media_communication", "comm", null, "미디어커뮤니케이션학과"),
    CULTURE_CONT("culture_content", "culturecontents", null, "문화콘텐츠학과"),

    MATH("math", "math", null, "수학과"),
    PHYSICS("physics", "phys", null, "물리학과"),
    CHEMICALS("chemicals", "chemi", null, "화학과"),

    ARCHITECTURE("architecture", "caku", null, "건축학부"),

    CIVIL_ENV("civil_environment", "cee", null, "사회환경공학부"),
    MECH_AERO("mechanical_aerospace", "mae", null, "기계항공공학부"),
    ELEC_ELEC("electrical_electronics", "ee", null, "전자전기공학부"),
    CHEMI_DIV("chemical_division", "chemeng", null, "화학공학부"),
    COMPUTER("computer_science", "cse", null, "컴퓨터공학부"),
    INDUSTRIAL("industrial", "kies", null, "산업공학과"),
    ADV_INDUSTRIAL("advanced_industrial", "aif", null, "신산업융합학과"),
    BIOLOGICAL("biological", "microbio", null, "생물공학과"),
    KBEAUTY("kbeauty_industry_fusion", "kbeauty", null, "K뷰티산업융합학과"),
    MECHA_ROBOT_AUTO("mechanical_robot_automotive", "me", null, "기계로봇자동차공학부"),
    MATERIAL("materials_science", "mse", null, "재료공학과"),
    AERO_MOBILITY("aerospace_mobility", "aeroeng", null, "항공우주모빌리티공학과"),

    POLITICS("political_science", "kupol", null, "정치외교학과"),
    ECONOMICS("economics", "econ", null, "경제학과"),
    ADMINISTRATION("public_administration", "kkupa", null, "행정학과"),
    INT_TRADE("international_trade", "itrade", null, "국제무역학과"),
    STATISTICS("applied_statistics", "stat", null, "응용통계학과"),
    DISCI_STUDIES("inter_disciplinary_studies", "dola", null, "융합인재학과"),
    GLOBAL_BUSI("global_business", "dois", null, "글로벌비즈니스학과"),

    BUIS_ADMIN("business_administration", "biz", null, "경영학과"),
    TECH_BUSI("technological_business", "mot", null, "기술경영학과"),

    REAL_ESTATE("real_estate", "kure", "rest", "부동산학과"),

    ENERGY("energy", "energy", null, "미래에너지공학과"),
    SMART_VEHICLE("smart_vehicle", "smartvehicle", null, "스마트운행체공학과"),
    SMART_ICT("smart_ict_convergence", "sicte", null, "스마트ICT융합공학과"),
    COSMETICS("cosmetics", "cosmetics", null, "화장품공학과"),
    STEM_REGEN("stem_cell_regeneration_bio_technology", "scrb", null, "줄기세포재생공학과"),
    BIO_MEDICAL("bio_medical_science", "bmse", null, "의생명공학과"),
    SYSTEM_BIO_TECH("system_bio_technology", "kusysbt", null, "시스템생명공학과"),
    INT_BIO_TECH("intergrative_bio_technology", "ibb", null, "융합생명공학과"),

    BIO_SCIENCE("biological_science", "biology", null, "생명과학특성학과"),
    ANIMAL_SCIENCE("animal_science_technology", "anis", null, "동물자원과학과"),
    CROP_SCIENCE("crop_science", "cropscience", null, "식량자원과학과"),
    ANIMAL_RESOURCES("animal_resources_food_science_bio_technology", "foodbio", null, "축산식품생명공학과"),
    FOOD_MARKETING("food_marketing_safety", "kufsm", null, "식품유통공학과"),
    ENV_HEALTH_SCIENCE("environment_health_science", "ehs", null, "환경보건과학과"),
    FORESTRY_LANDSCAPE_ARCH("forestry_landscape_architecture", "fla", null, "산림조경학과"),

    VET_MEDICINE("veterinary_medicine", "vet", null, "수의학과"),

    COMM_DESIGN("communication_design", "kucd", null, "커뮤니케이션디자인학과"),
    IND_DESIGN("industrial_design", "designid", null, "산업디자인학과"),
    APPAREL_DESIGN("apparel_design", "apparel", null, "의상디자인학과"),
    LIVING_DESIGN("living_design", "livingdesign", null, "리빙디자인학과"),
    CONT_ART("contemporary_art", "contemporaryart", null, "현대미술학과"),
    MOV_IMAGE("moving_image_film", "movingimages", null, "영상학과"),
    MEDIA_ACTING("media_acting", "mediaacting", null, "매체연기학과"),

    JAPANESE_EDU("japanese_education", "japan", null, "일어교육과"),
    MATH_EDU("mathematics_education", "mathedu", null, "수학교육과"),
    PHY_EDU("physical_education", "kupe", null, "체육교육과"),
    MUSIC_EDU("music_education", "music", null, "음악교육과"),
    EDU_TECH("education_technology", "edutech", null, "교육공학과"),
    ENGLISH_EDU("english_education", "englishedu", null, "영어교육과"),
    EDUCATION("education", "edu", null, "교직과"),

    ELE_EDU_CENTER("elective_education_center", "sgedu", null, "교양교육센터"),
    VOLUNTEER("volunteer_center", "kuvolunteer", null, "사회봉사센터"),
    LIBERAL_STUDIES("liberal_studies", "kusls", null, "KU자유전공학부"),

    ;
    private static final Map<String, String> NAME_MAP;
    private static final Map<String, String> HOST_PREFIX_MAP;
    private static final Map<String, String> KOR_NAME_MAP;

    static {
        NAME_MAP = Collections.unmodifiableMap(Arrays.stream(DepartmentName.values())
                .collect(Collectors.toMap(DepartmentName::getName, DepartmentName::name)));

        HOST_PREFIX_MAP = Collections.unmodifiableMap(
                Arrays.stream(DepartmentName.values())
                        .flatMap(d -> Stream.of(d.hostPrefix, d.fallbackHostPrefix)
                                .filter(Objects::nonNull)
                                .map(prefix -> Map.entry(prefix, d.name()))
                        )
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (a, b) -> a
                        ))
        );

        KOR_NAME_MAP = Collections.unmodifiableMap(Arrays.stream(DepartmentName.values())
                .collect(Collectors.toMap(DepartmentName::getKorName, DepartmentName::name)));
    }

    private final String name;
    private final String hostPrefix;
    private final String fallbackHostPrefix;
    private final String korName;

    DepartmentName(String name, String hostPrefix, String fallbackHostPrefix, String korName) {
        this.name = name;
        this.hostPrefix = hostPrefix;
        this.fallbackHostPrefix = fallbackHostPrefix;
        this.korName = korName;
    }

    public static DepartmentName fromName(String name) {
        String findName = Optional.ofNullable(NAME_MAP.get(name))
                .orElseThrow(() -> new NotFoundException(DEPARTMENT_NOT_FOUND));
        return DepartmentName.valueOf(findName);
    }

    public static DepartmentName fromHostPrefix(String hostPrefix) {
        String findHostPrefix = Optional.ofNullable(HOST_PREFIX_MAP.get(hostPrefix))
                .orElseThrow(() -> new NotFoundException(DEPARTMENT_NOT_FOUND));
        return DepartmentName.valueOf(findHostPrefix);
    }

    public static DepartmentName fromKor(String korName) {
        String findKorName = Optional.ofNullable(KOR_NAME_MAP.get(korName))
                .orElseThrow(() -> new NotFoundException(DEPARTMENT_NOT_FOUND));
        return DepartmentName.valueOf(findKorName);
    }
}

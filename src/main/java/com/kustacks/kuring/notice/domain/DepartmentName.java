package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustacks.kuring.common.exception.code.ErrorCode.DEPARTMENT_NOT_FOUND;

@Getter
public enum DepartmentName {

    KOREAN("korean", "korea", "korea", "국어국문학과"),
    ENGLISH("english", "english","english", "영어영문학과"),
    CHINESE("chinese", "china","china", "중어중문학과"),
    PHILOSOPHY("philosophy", "philo","philo", "철학과"),
    HISTORY("khistory", "khistory","khistory", "사학과"),
    GEOLOGY("geology", "kugeo","kugeo", "지리학과"),
    MEDIA_COMM("media_communication", "comm", "comm", "미디어커뮤니케이션학과"),
    CULTURE_CONT("culture_content", "culturecontents","culturecontents", "문화콘텐츠학과"),

    MATH("math", "math","math", "수학과"),
    PHYSICS("physics", "phys","phys", "물리학과"),
    CHEMICALS("chemicals", "chemi","chemi", "화학과"),

    ARCHITECTURE("architecture", "caku","caku", "건축학부"),

    CIVIL_ENV("civil_environment", "cee","cee", "사회환경공학부"),
    MECH_AERO("mechanical_aerospace", "mae","mae", "기계항공공학부"),
    ELEC_ELEC("electrical_electronics", "ee", "ee","전자전기공학부"),
    CHEMI_DIV("chemical_division", "chemeng","chemeng", "화학공학부"),
    COMPUTER("computer_science", "cse", "cse", "컴퓨터공학부"),
    INDUSTRIAL("industrial", "kies", "kies", "신산업공학과"),
    ADV_INDUSTRIAL("advanced_industrial", "aif","aif", "신산업융합학과"),
    BIOLOGICAL("biological", "microbio","microbio", "생물공학과"),
    KBEAUTY("kbeauty_industry_fusion", "kbeauty","kbeauty", "K뷰티산업융합학과"),

    POLITICS("political_science", "kupol","kupol", "정치외교학과"),
    ECONOMICS("economics", "econ", "econ", "경제학과"),
    ADMINISTRATION("public_administration", "kkupa","kkupa", "행정학과"),
    INT_TRADE("international_trade", "itrade", "itrade", "국제무역학과"),
    STATISTICS("applied_statistics", "stat","stat", "응용통계학과"),
    DISCI_STUDIES("inter_disciplinary_studies", "dola","dola", "융합인재학과"),
    GLOBAL_BUSI("global_business", "dois", "dois", "글로벌비즈니스학과"),

    BUIS_ADMIN("business_administration", "biz","biz", "경영학과"),
    TECH_BUSI("technological_business", "mot", "mot", "기술경영학과"),

    REAL_ESTATE("real_estate", "rest","rest",  "부동산학과"),

    ENERGY("energy", "energy", "energy", "미래에너지공학과"),
    SMART_VEHICLE("smart_vehicle", "smartvehicle","smartvehicle", "스마트운행체공학과"),
    SMART_ICT("smart_ict_convergence", "sicte","sicte", "스마트ICT융합공학과"),
    COSMETICS("cosmetics", "cosmetics","cosmetics", "화장품공학과"),
    STEM_REGEN("stem_cell_regeneration_bio_technology", "scrb", "scrb", "줄기세포재생공학과"),
    BIO_MEDICAL("bio_medical_science", "bmse","bmse",  "의생명공학과"),
    SYSTEM_BIO_TECH("system_bio_technology", "kusysbt", "kusysbt", "시스템생명공학과"),
    INT_BIO_TECH("intergrative_bio_technology", "ibb","ibb", "융합생명공학과"),

    BIO_SCIENCE("biological_science", "biology","biology", "생명과학특성학과"),
    ANIMAL_SCIENCE("animal_science_technology", "anis", "anis", "동물자원과학과"),
    CROP_SCIENCE("crop_science", "cropscience","cropscience", "식량자원과학과"),
    ANIMAL_RESOURCES("animal_resources_food_science_bio_technology", "foodbio","foodbio", "축산식품생명공학과"),
    FOOD_MARKETING("food_marketing_safety", "kufsm","kufsm", "식품유통공학과"),
    ENV_HEALTH_SCIENCE("environment_health_science", "ehs","ehs", "환경보건과학과"),
    FORESTRY_LANDSCAPE_ARCH("forestry_landscape_architecture", "fla","fla", "산림조경학과"),

    VET_MEDICINE("veterinary_medicine", "vet","vet", "수의학과"),

    COMM_DESIGN("communication_design", "kucd", "kucd", "커뮤니케이션디자인학과"),
    IND_DESIGN("industrial_design", "designid", "designid", "산업디자인학과"),
    APPAREL_DESIGN("apparel_design", "apparel","apparel", "의상디자인학과"),
    LIVING_DESIGN("living_design", "livingdesign", "livingdesign", "리빙디자인학과"),
    CONT_ART("contemporary_art", "contemporaryart","contemporaryart", "현대미술학과"),
    MOV_IMAGE("moving_image_film", "movingimages", "movingimages", "영상영화학과"),

    JAPANESE_EDU("japanese_education", "japan","japan", "일어교육과"),
    MATH_EDU("mathematics_education", "mathedu", "mathedu", "수학교육과"),
    PHY_EDU("physical_education", "kupe", "kupe", "체육교육과"),
    MUSIC_EDU("music_education", "music", "music", "음악교육과"),
    EDU_TECH("education_technology", "edutech","edutech", "교육공학과"),
    ENGLISH_EDU("english_education", "englishedu","englishedu", "영어교육과"),
    EDUCATION("education", "edu","edu", "교직과"),

    ELE_EDU_CENTER("elective_education_center", "sgedu", "sgedu", "교양교육센터"),
    VOLUNTEER("volunteer_center", "kuvolunteer","kuvolunteer", "사회봉사센터"),
    LIBERAL_STUDIES("liberal_studies", "kusls","kusls", "KU자유전공학부"),

    // 석사
    COMPUTER_GRADUATE("computer_science_graduate", "cse_grad", "cse","컴퓨터공학부(석사)"),

    ;
    private static final Map<String, String> NAME_MAP;
    private static final Map<String, String> HOST_PREFIX_MAP;
    private static final Map<String, String> KOR_NAME_MAP;

    static {
        NAME_MAP = Collections.unmodifiableMap(Arrays.stream(DepartmentName.values())
                        .collect(Collectors.toMap(DepartmentName::getName, DepartmentName::name)));

        HOST_PREFIX_MAP = Collections.unmodifiableMap(Arrays.stream(DepartmentName.values())
                .collect(Collectors.toMap(DepartmentName::getHostPrefix, DepartmentName::name)));

        KOR_NAME_MAP = Collections.unmodifiableMap(Arrays.stream(DepartmentName.values())
                .collect(Collectors.toMap(DepartmentName::getKorName, DepartmentName::name)));
    }

    private final String name;
    private final String hostPrefix;
    private final String urlPrefix; // 실제 url에 쓰이는 prefix
    private final String korName;

    DepartmentName(String name, String hostPrefix, String urlPrefix, String korName) {
        this.name = name;
        this.hostPrefix = hostPrefix;
        this.urlPrefix = urlPrefix;
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

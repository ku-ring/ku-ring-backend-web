package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.error.DomainLogicException;
import lombok.Getter;

import java.util.Arrays;

import static com.kustacks.kuring.common.error.ErrorCode.DEPARTMENT_NOT_FOUND;

@Getter
public enum DepartmentName {

    KOREAN("korean", "korea", "국어국문학과"),
    ENGLISH("english", "english", "영어영문학과"),
    CHINESE("chinese", "china", "중어중문학과"),
    PHILOSOPHY("philosophy", "philo", "철학과"),
    HISTORY("khistory", "khistory", "사학과"),
    GEOLOGY("geology", "kugeo", "지리학과"),
    MEDIA_COMM("media_communication", "comm", "미디어커뮤니케이션학과"),
    CULTURE_CONT("culture_content", "culturecontents", "문화콘텐츠학과"),

    MATH("math", "math", "수학과"),
    PHYSICS("physics", "phys", "물리학과"),
    CHEMICALS("chemicals", "chemi", "화학과"),

    ARCHITECTURE("architecture", "caku", "건축학부"),

    CIVIL_ENV("civil_environment", "cee", "사회환경공학부"),
    MECH_AERO("mechanical_aerospace", "mae", "기계항공공학부"),
    ELEC_ELEC("electrical_electronics", "ee", "전자전기공학부"),
    CHEMI_DIV("chemical_division", "chemeng", "화학공학부"),
    COMPUTER("computer_science", "cse", "컴퓨터공학부"),
    INDUSTRIAL("industrial", "kies", "신산업공학과"),
    ADV_INDUSTRIAL("advanced_industrial", "aif", "신산업융합학과"),
    BIOLOGICAL("biological", "microbio", "생물공학과"),
    KBEAUTY("kbeauty_industry_fusion", "kbeauty", "K뷰티산업융합학과"),

    POLITICS("political_science", "kupol", "정치외교학과"),
    ECONOMICS("economics", "econ", "경제학과"),
    ADMINISTRATION("public_administration", "kkupa", "행정학과"),
    INT_TRADE("international_trade", "itrade", "국제무역학과"),
    STATISTICS("applied_statistics", "stat", "응용통계학과"),
    DISCI_STUDIES("inter_disciplinary_studies", "dola", "융합인재학과"),
    GLOBAL_BUSI("global_business", "dois", "글로벌비즈니스학과"),

    BUIS_ADMIN("business_administration", "biz", "경영학과"),
    TECH_BUSI("technological_business", "mot", "기술경영학과"),

    REAL_ESTATE("real_estate", "rest", "부동산학과"),

    ENERGY("energy", "energy", "미래에너지공학과"),
    SMART_VEHICLE("smart_vehicle", "smartvehicle", "스마트운행체공학과"),
    SMART_ICT("smart_ict_convergence", "sicte", "스마트ICT융합공학과"),
    COSMETICS("cosmetics", "cosmetics", "화장품공학과"),
    STEM_REGEN("stem_cell_regeneration_bio_technology", "scrb", "줄기세포재생공학과"),
    BIO_MEDICAL("bio_medical_science", "bmse", "의생명공학과"),
    SYSTEM_BIO_TECH("system_bio_technology", "kusysbt", "시스템생명공학과"),
    INT_BIO_TECH("intergrative_bio_technology", "ibb", "융합생명공학과"),

    BIO_SCIENCE("biological_science", "biology", "생명과학특성학과"),
    ANIMAL_SCIENCE("animal_science_technology", "anis", "동물자원과학과"),
    CROP_SCIENCE("crop_science", "cropscience", "식량자원과학과"),
    ANIMAL_RESOURCES("animal_resources_food_science_bio_technology", "foodbio", "축산식품생명공학과"),
    FOOD_MARKETING("food_marketing_safety", "kufsm", "식품유통공학과"),
    ENV_HEALTH_SCIENCE("environment_health_science", "ehs", "환경보건과학과"),
    FORESTRY_LANDSCAPE_ARCH("forestry_landscape_architecture", "fla", "산림조경학과"),

    VET_MEDICINE("veterinary_medicine", "vet", "수의학과"),

    COMM_DESIGN("communication_design", "kucd", "커뮤니케이션디자인학과"),
    IND_DESIGN("industrial_design", "designid", "산업디자인학과"),
    APPAREL_DESIGN("apparel_design", "apparel", "의상디자인학과"),
    LIVING_DESIGN("living_design", "livingdesign", "리빙디자인학과"),
    CONT_ART("contemporary_art", "contemporaryart", "현대미술학과"),
    MOV_IMAGE("moving_image_film", "movingimages", "영상영화학과"),

    JAPANESE_EDU("japanese_education", "japan", "일어교육과"),
    MATH_EDU("mathematics_education", "mathedu", "수학교육과"),
    PHY_EDU("physical_education", "kupe", "체육교육과"),
    MUSIC_EDU("music_education", "music", "음악교육과"),
    EDU_TECH("education_technology", "edutech", "교육공학과"),
    ENGLISH_EDU("english_education", "englishedu", "영어교육과"),
    EDUCATION("education", "edu", "교직과"),

    ELE_EDU_CENTER("elective_education_center", "sgedu", "교양교육센터"),
    VOLUNTEER("volunteer_center", "kuvolunteer", "사회봉사센터");

    private final String name;
    private final String hostPrefix;
    private final String korName;

    DepartmentName(String name, String hostPrefix, String korName) {
        this.name = name;
        this.hostPrefix = hostPrefix;
        this.korName = korName;
    }

    public boolean isSameHostPrefix(String name) {
        return this.hostPrefix.equals(name);
    }

    public boolean isSameKorName(String name) {
        return this.korName.equals(name);
    }

    public static DepartmentName fromHostPrefix(String hostPrefix) {
        return Arrays.stream(DepartmentName.values())
                .filter(d -> d.isSameHostPrefix(hostPrefix))
                .findFirst()
                .orElseThrow(() -> new DomainLogicException(DEPARTMENT_NOT_FOUND));
    }

    public static DepartmentName fromKor(String departmentName) {
        return Arrays.stream(DepartmentName.values())
                .filter(d -> d.isSameKorName(departmentName))
                .findFirst()
                .orElseThrow(() -> new DomainLogicException(DEPARTMENT_NOT_FOUND));
    }
}

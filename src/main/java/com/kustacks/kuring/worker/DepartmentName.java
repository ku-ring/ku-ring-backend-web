package com.kustacks.kuring.worker;

import lombok.Getter;

@Getter
public enum DepartmentName {

    KOREAN("korean", "kor", "국어국문학과"),
    ENGLISH("english", "eng", "영어영문학과"),
    CHINESE("chinese", "chi", "중어중문학과"),
    PHILOSOPHY("philosophy", "phi", "철학과"),
    HISTORY("khistory", "khis", "사학과"),
    GEOLOGY("geology", "geo", "지리학과"),
    MEDIA_COMM("media_communication", "comm", "미디어커뮤니케이션학과"),
    CULTURE_CONT("culture_content", "cul", "문화컨텐츠학과"),

    MATH("math", "math", "수학과"),
    PHYSICS("physics", "phys", "물리학과"),
    CHEMICALS("chemicals", "chemi", "화학과"),

    ARCHITECTURE("architecture", "arch", "건축학부"),

    CIVIL_ENV("civil_environment", "cvenv", "사회환경공학부"),
    MECH_AERO("mechanical_aerospace", "maero", "기계항공공학부"),
    ELEC_ELEC("electrical_electronics", "ee", "전자전기공학부"),
    CHEMI_DIV("chemical_division", "chemidiv", "화학공학부"),
    COMPUTER("computer_science", "com", "컴퓨터공학부"),
    INDUSTRIAL("industrial", "indu", "신산업공학과"),
    ADV_INDUSTRIAL("advanced_industrial", "advindu", "신산업융합학과"),
    BIOLOGICAL("biological", "bio", "생물공학과"),
    KBEAUTY("kbeauty_industry_fusion", "kbeauty", "K뷰티산업융합학과"),

    POLITICS("political_science", "pol", "정치외교학과"),
    ECONOMICS("economics", "eco", "경제학과"),
    ADMINISTRATION("public_administration", "adm", "행정학과"),
    INT_TRADE("international_trade", "trde", "국제무역학과"),
    STATISTICS("applied_statistics", "stat", "응용통계학과"),
    DISCI_STUDIES("inter_disciplinary_studies", "dis", "융합인재학과"),
    GLOBAL_BUSI("global_business", "gbuis", "글로벌비즈니스학과"),

    BUIS_ADMIN("business_administration", "badm", "경영학과"),
    TECH_BUSI("technological_business", "techbuis", "기술경영학과"),

    REAL_ESTATE("real_estate", "rest", "부동산학과"),

    ENERGY("energy", "egy", "미래에너지공학과"),
    SMART_VEHICLE("smart_vehicle", "smtveh", "스마트운행체공학과"),
    SMART_ICT("smart_ict_convergence", "smtict", "스마트ICT융합공학과"),
    COSMETICS("cosmetics", "cmt", "화장품공학과"),
    STEM_REGEN("stem_cell_regeneration_bio_technology", "stem", "줄기세포재생공학과"),
    BIO_MEDICAL("bio_medical_science", "bmed", "의생명공학과"),
    SYSTEM_BIO_TECH("system_bio_technology", "sysbio", "시스템생명공학과"),
    INT_BIO_TECH("intergrative_bio_technology", "intbio", "융합생명공학과"),

    BIO_SCIENCE("biological_science", "biosci", "생명과학특성학과"),
    ANIMAL_SCIENCE("animal_science_technology", "anitech", "동물자원과학과"),
    CROP_SCIENCE("crop_science", "cropsci", "식량자원과학과"),
    ANIMAL_RESOURCES("animal_resources_food_science_bio_technology", "anires", "축산식품생명공학과"),
    FOOD_MARKETING("food_marketing_safety", "food", "식품유통공학과"),
    ENV_HEALTH_SCIENCE("environment_health_science", "envhlth", "환경보건과학과"),
    FORESTRY_LANDSCAPE_ARCH("forestry_landscape_architecture", "fla", "산림조경학과"),

    VET_PRE("pre_veterinary", "prevet", "수의예과"),
    VET_MEDICINE("veterinary_medicine", "vetmed", "수의학과"),

    COMM_DESIGN("communication_design", "commdes", "커뮤니케이션디자인학과"),
    IND_DESIGN("industrial_design", "inddes", "산업디자인학과"),
    APPAREL_DESIGN("apparel_design", "appdes", "의상디자인학과"),
    LIVING_DESIGN("living_design", "livdes", "리빙디자인학과"),
    CONT_ART("contemporary_art", "contart", "현대미술학과"),
    MOV_IMAGE("moving_image_film", "movimg", "영상영화학과"),

    JAPANESE_EDU("japanese_education", "jpaedu", "일어교육과"),
    MATH_EDU("mathematics_education", "mathedu", "수학교육과"),
    PHY_EDU("physical_education", "phyedu", "체육교육과"),
    MUSIC_EDU("music_education", "musedu", "음악교육과"),
    EDU_TECH("education_technology", "edutech", "교육공학과"),
    ENGLISH_EDU("english_education", "engedu", "영어교육과"),
    EDUCATION("education", "edu", "교직과"),

    ELE_EDU_CENTER("elective_education_center", "eec", "교양교육센터"),
    VOLUNTEER("volunteer_center", "vc", "사회봉사센터");

    private final String name;
    private final String shortName;
    private final String korName;

    DepartmentName(String name, String shortName, String korName) {
        this.name = name;
        this.shortName = shortName;
        this.korName = korName;
    }
}

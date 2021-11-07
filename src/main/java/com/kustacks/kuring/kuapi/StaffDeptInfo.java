package com.kustacks.kuring.kuapi;

import lombok.Getter;

@Getter
public enum StaffDeptInfo {

    // 문과대학
    OFFICE_LIBERAL_ARTS("102811", "문과대학 행정실", "문과대학"),
    KOREAN("121253", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_01_01_tab01.jsp", "국어국문학과", "문과대학"),
    ENGLISH("121254", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_02_01_tab01.jsp", "영어영문학과", "문과대학"),
    CHINESE("121255", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_02_02_tab01.jsp", "중어중문학과", "문과대학"),
    PHILOSOPHY("121256", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_01_02_tab01.jsp", "철학과", "문과대학"),
    HISTORY("121257", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_01_03_tab01.jsp", "사학과", "문과대학"),
    GEOLOGY("127107", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_02_01_05_tab01.jsp", "지리학과", "문과대학"),
    MEDIA_COMMUNICATION("122281", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_03_02_tab01.jsp", "미디어커뮤니케이션과", "문과대학"),
    CULTURE_CONTENT("121259", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_01_03_04_tab01.jsp", "문화콘텐츠학과", "문과대학"),

    // 이과대학
    OFFICE_SCIENCE("105151", "이과대학 행정실", "이과대학"),
    MATHEMATICS("121260", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_02_01_01_tab01.jsp", "수학과", "이과대학"),
    PHYSICS("126783", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_02_01_02_tab01.jsp", "물리학과", "이과대학"),
    CHEMISTRY("121261", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_02_01_03_tab01.jsp", "화학과", "이과대학"),

    // 건축대학
    OFFICE_ARCHITECTURE("105301", "건축대학 행정실", "건축대학"),
    ARCHITECTURE("127320", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_03_01_04_tab01.jsp", "건축학부", "건축대학"),

    // 공과대학
    OFFICE_ENGINEERING("103161", "공과대학 행정실", "공과대학"),
    SOCIAL_ENVIRONMENT("127108", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_11_01_01_tab01.jsp", "사회환경공학부", "공과대학"),
    MACHINE_AEROSPACE("127427", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_10_01_tab01.jsp", "기계항공공학부", "공과대학"),
    ELECTRICAL_ELECTRONIC("127110", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_03_01_tab01.jsp", "전기전자공학부", "공과대학"),
    CHEMICAL("127111", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_04_02_tab01.jsp", "화학공학부", "공과대학"),
    COMPUTER_SCIENCE("127428", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_05_01_04_tab01.jsp", "컴퓨터공학부", "공과대학"),
    INDUSTRIAL("127430", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_05_01_tab01.jsp", "산업공학과", "공과대학"),
    NEW_INDUSTRIAL_CONVERGENCE("127431", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_15_01_05_tab01.jsp", "신산업융합학과", "공과대학"),
    BIOTECHNOLOGY("122055", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_04_03_tab01.jsp", "생물공학과", "공과대학"),
    KBEAUTY_INDUSTRIAL_CONVERGENCE("127432", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_04_08_01_tab01.jsp", "K뷰티산업융합학과", "공과대학"),

    // 사회과학대학
    OFFICE_SOCIAL_SCIENCE("127127", "사회과학대학 행정실", "사회과학대학"),
    POLITICAL_DIPLOMACY("127120", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_06_01_01_tab01.jsp", "정치외교학과", "사회과학대학"),
    ECONOMICS("127121", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_08_01_01_tab01.jsp", "경제학과", "사회과학대학"),
    PUBLIC_ADMINISTRATION("127122", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_06_01_02_tab01.jsp", "행정학과", "사회과학대학"),
    INTERNATIONAL_TRADE("127123", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_08_01_02_tab01.jsp", "국제무역학과", "사회과학대학"),
    APPLIED_STATISTICS("127124", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_08_02_01_tab01.jsp", "응용통계학과", "사회과학대학"),
    CONVERGENCE_TALENT("127125", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_15_01_02_tab01.jsp", "융합인재학과", "사회과학대학"),
    GLOBAL_BUSINESS("127126", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_15_01_03_tab01.jsp", "글로벌비즈니스학과", "사회과학대학"),

    // 경영대학
    OFFICE_BUSINESS("105571", "경영대학 행정실", "경영대학"),
    BUSINESS("126780", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_09_01_01_tab01.jsp", "경영학과", "경영대학"),
    TECHNOLOGY_MANAGEMENT("121174", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_09_01_03_tab01.jsp", "기술경영학과", "경영대학"),

    // 부동산과학원
    OFFICE_REAL_ESTATE_SCIENCE("127462", "부동산과학원 행정실", "부동산과학원"),
    REAL_ESTATE("127426", "http://www.realestate.ac.kr/gb/bbs/board.php?bo_table=faculty", "부동산학과", "부동산과학원"),

    // KU융합과학기술원
    OFFICE_CONVERGENCE_SCIENCE_TECHNOLOGY("126961", "KU융합과학기술원 행정실", "KU융합과학기술원"),
    FUTURE_ENERGY("126913", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_03_tab01.jsp", "미래에너지공학과", "KU융합과학기술원"),
    SMART_VEHICLE("126914", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_04_tab01.jsp", "스마트운행체공학과", "KU융합과학기술원"),
    SMART_ICT_CONVERGENCE("126915", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_05_tab01.jsp", "스마트ICT융합공학과", "KU융합과학기술원"),
    COSMETIC("126916", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_06_tab01.jsp", "화장품공학과", "KU융합과학기술원"),
    STEMCELL_REGENERATION("126917", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_07_tab01.jsp", "줄기세포재생공학과", "KU융합과학기술원"),
    BIOMEDICAL("126918", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_10_tab01.jsp", "의생명공학과", "KU융합과학기술원"),
    SYSTEM_BIOTECHNOLOGY("126919", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_08_tab01.jsp", "시스템생명공학과", "KU융합과학기술원"),
    CONVERGENCE_BIOTECHNOLOGY("126920", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_20_01_09_tab01.jsp", "융합생명공학과", "KU융합과학기술원"),

    // 상허생명과학대학
    OFFICE_SANGHUO_BIOLOGY("126960", "상허생명과학대학 행정실", "상허생명과학대학"),
    BIOLOGICAL_SCIENCE("126906", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=6182", "생명과학특성학과", "상허생명과학대학"),
    ANIMAL_SCIENCE_TECHNOLOGY("126907", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=17673919", "동물자원학과", "상허생명과학대학"),
    CROP_SCIENCE("126908", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=15766997", "식량자원과학과", "상허생명과학대학"),
    FOOD_SCIENCE_ANIMAL_RESOURCES_BIOTECHNOLOGY("126909", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=15632573", "축산식품생명공학과", "상허생명과학대학"),
    FOOD_MARKETING_SAFETY("126910", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=15827578", "식품유통공학과", "상허생명과학대학"),
    ENVIRONMENTAL_HEALTH_SCIENCE("126911", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=13900359", "환경보건과학과", "상허생명과학대학"),
    FORESTRY_LANDSCAPE_ARCHITECTURE("126912", "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do?pfForumId=15766919", "산림조경학과", "상허생명과학대학"),

    // 수의과대학
    OFFICE_VETERINARY("105081", "수의과대학 행정실", "수의과대학"),
    PRE_VETERINARY("105091", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_12_01_01_tab01.jsp", "수의예과", "수의과대학"),
    VETERINARY_MEDICINE("105101", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_12_01_02_tab01.jsp", "수의학과", "수의과대학"),

    // 예술디자인대학
    OFFICE_ART_DESIGN("122069", "예술디자인대학 행정실", "예술디자인대학"),
    COMMUNICATION_DESIGN("122402", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_01_tab01.jsp", "커뮤니케이션디자인학과", "예술디자인대학"),
    INDUSTRY_DESIGN("122403", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_02_tab01.jsp", "산업디자인학과", "예술디자인대학"),
    CLOTH_DESIGN("122404", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_03_tab01.jsp", "의상디자인학과", "예술디자인대학"),
    LIVING_DESIGN("126781", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_05_tab01.jsp", "리빙디자인학과", "예술디자인대학"),
    MODERN_ART("122406", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_02_01_tab01.jsp", "현대미술학과", "예술디자인대학"),
    VISUAL_FILM("127128", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_02_05_tab01.jsp", "영상영화학과", "예술디자인대학"),

    // 사범대학
    OFFICE_EDUCATION("104971", "사범대학 행정실", "사범대학"),
    JAPANESE_EDU("104981", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_01_tab01.jsp", "일어교육과", "사범대학"),
    MATH_EDU("104991", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_02_tab01.jsp", "수학교육과", "사범대학"),
    PHYSICAL_EDU("105001", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_03_tab01.jsp", "체육교육과", "사범대학"),
    MUSIC_EDU("105011", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_04_tab01.jsp", "음악교육과", "사범대학"),
    EDUCATIONAL_ENGINEERING("105031", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_05_tab01.jsp", "교육공학과", "사범대학"),
    ENGLISH_EDU("121175", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_06_tab01.jsp", "영어교육과", "사범대학"),
    TEACHING("105041", "http://www.konkuk.ac.kr/jsp/Coll/coll_01_14_01_07_tab01.jsp", "교직과", "사범대학"),

    // 상허교양대학
    OFFICE_SANGHUO_LIBERAL("126842", "상허교양대학 행정실", "상허교양대학"),
    LIBERAL_EDUCATION_CENTER("126952", "교양교육센터", "상허교양대학"),
    LIBERAL_RESEARCH_EVALUATION_CENTER("127423", "교양연구평가센터", "상허교양대학"),
    COMMUNITY_SERVICE_CENTER("127424", "사회봉사센터", "상허교양대학"),
    INTERNATIONAL_STUDY("127461", "국제학부", "상허교양대학");

    private final String code;
    private final String url;
    private final String name;
    private final String collegeName;

    StaffDeptInfo(String code, String name, String collegeName) {
        this.code = code;
        this.url = null;
        this.name = name;
        this.collegeName = collegeName;
    }

    StaffDeptInfo(String code, String url, String name, String collegeName) {
        this.code = code;
        this.url = url;
        this.name = name;
        this.collegeName = collegeName;
    }
}

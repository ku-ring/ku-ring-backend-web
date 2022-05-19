package com.kustacks.kuring.config;

import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DatabaseInitConfig {

    private final CategoryRepository categoryRepository;
    private final List<Category> categories;
    private final int TOTAL_CATEGORY_SIZE = 71;

    public DatabaseInitConfig(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categories = new ArrayList<>(TOTAL_CATEGORY_SIZE);
        initCategories();
    }

    @Bean
    public ApplicationRunner dbInitApplicationRunner() {
        return args -> categoryRepository.saveAllAndFlush(categories);
    }

    /*
        Category 테이블의 기본 데이터를 초기화하는 메서드
     */
    private void initCategories() {
        // 루트 카테고리
        // kuis, major
        Category kuis = Category.builder().parent(null).isLeaf(false).korName("").shortName("").name("kuis").build();
        Category major = Category.builder().parent(null).isLeaf(false).korName("").shortName("").name("major").build();
        categories.add(kuis);
        categories.add(major);


        // kuis의 leaf 카테고리
        // 학사, 장학, 취창업, 국제, 학생, 산학, 일반, 도서관
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("bachelor").korName("학사").shortName("bch").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("scholarship").korName("장학").shortName("sch").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("employment").korName("취창업").shortName("emp").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("national").korName("국제").shortName("nat").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("student").korName("학생").shortName("stu").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("industry_university").korName("산학").shortName("ind").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("normal").korName("일반").shortName("nor").build());
        categories.add(Category.builder().parent(kuis).isLeaf(true).name("library").korName("도서관").shortName("lib").build());
        
        // major의 leaf 카테고리
        // 59개 (63개 - 생명과학특성학과, 수의예과, 커뮤니케이션디자인 제외)

        // 문과대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("korean").korName("국어국문학과").shortName("kor").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("english").korName("영어영문학과").shortName("eng").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("chinese").korName("중어중문학과").shortName("chi").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("philosophy").korName("철학과").shortName("phi").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("khistory").korName("사학과").shortName("khis").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("geology").korName("지리학과").shortName("geo").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("media_communication").korName("미디어커뮤니케이션학과").shortName("comm").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("culture_content").korName("문화컨텐츠학과").shortName("cul").build());

        // 이과대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("math").korName("수학과").shortName("math").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("physics").korName("물리학과").shortName("phys").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("chemicals").korName("화학과").shortName("chemi").build());
        
        // 건축대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("architecture").korName("건축학부").shortName("arch").build());

        // 공과대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("civil_environment").korName("사회환경공학부").shortName("evenv").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("mechanical_aerospace").korName("기계항공공학부").shortName("maero").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("electrical_electronics").korName("전자전기공학부").shortName("ee").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("chemical_division").korName("화학공학부").shortName("chemidiv").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("computer_science").korName("컴퓨터공학부").shortName("com").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("industrial").korName("신산업공학과").shortName("indu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("advanced_industrial").korName("신산업융합학과").shortName("advindu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("biological").korName("생물공학과").shortName("bio").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("kbeauty_industry_fusion").korName("K뷰티산업융합학과").shortName("kbeauty").build());
        
        // 사회과학대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("political_science").korName("정치외교학과").shortName("pol").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("economics").korName("경제학과").shortName("eco").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("public_administration").korName("행정학과").shortName("adm").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("international_trade").korName("국제무역학과").shortName("trde").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("applied_statistics").korName("응용통계학과").shortName("stat").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("inter_disciplinary_studies").korName("융합인재학과").shortName("dis").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("global_business").korName("글로벌비즈니스학과").shortName("gbuis").build());

        // 경영대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("business_administration").korName("경영학과").shortName("badm").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("technological_business").korName("기술경영학과").shortName("techbuis").build());

        // 부동산과학원
        categories.add(Category.builder().parent(major).isLeaf(true).name("real_estate").korName("부동산학과").shortName("rest").build());

        // KU융합과학기술원
        categories.add(Category.builder().parent(major).isLeaf(true).name("energy").korName("미래에너지공학과").shortName("egy").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("smart_vehicle").korName("스마트운행체공학과").shortName("smtveh").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("smart_ict_convergence").korName("스마트ICT융합공학과").shortName("smtict").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("cosmetics").korName("화장품공학과").shortName("cmt").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("stem_cell_regeneration_bio_technology").korName("줄기세포재생공학과").shortName("stem").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("bio_medical_science").korName("의생명공학과").shortName("bmed").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("system_bio_technology").korName("시스템생명공학과").shortName("sysbio").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("intergrative_bio_technology").korName("융합생명공학과").shortName("intbio").build());

        // 상허생명과학대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("biological_science").korName("생명과학특성학과").shortName("biosci").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("animal_science_technology").korName("동물자원과학과").shortName("anitech").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("crop_science").korName("식량자원과학과").shortName("cropsci").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("animal_resources_food_science_bio_technology").korName("축산식품생명공학과").shortName("anires").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("food_marketing_safety").korName("식품유통공학과").shortName("food").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("environment_health_science").korName("환경보건과학과").shortName("envhlth").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("forestry_landscape_architecture").korName("산림조경학과").shortName("fla").build());

        // 수의과대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("pre_veterinary").korName("수의예과").shortName("prevet").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("veterinary_medicine").korName("수의학과").shortName("vetmed").build());

        // 예술디자인대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("communication_design").korName("커뮤니케이션디자인학과").shortName("commdes").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("industrial_design").korName("산업디자인학과").shortName("inddes").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("apparel_design").korName("의상디자인학과").shortName("appdes").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("living_design").korName("리빙디자인학과").shortName("livdes").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("contemporary_art").korName("현대미술학과").shortName("contart").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("moving_image_film").korName("영상영화학과").shortName("movimg").build());
        
        // 사범대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("japanese_education").korName("일어교육과").shortName("jpaedu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("mathematics_education").korName("수학교육과").shortName("mathedu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("physical_education").korName("체육교육과").shortName("phyedu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("music_education").korName("음악교육과").shortName("musedu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("education_technology").korName("교육공학과").shortName("edutech").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("english_education").korName("영어교육과").shortName("engedu").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("education").korName("교직과").shortName("edu").build());

        // 상허교양대학
        categories.add(Category.builder().parent(major).isLeaf(true).name("elective_education_center").korName("교양교육센터").shortName("eec").build());
        categories.add(Category.builder().parent(major).isLeaf(true).name("volunteer_center").korName("사회봉사센터").shortName("vc").build());
    }
}

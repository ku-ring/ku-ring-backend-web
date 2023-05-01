package com.kustacks.kuring.category.domain;

import com.kustacks.kuring.config.QueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(QueryDslConfig.class)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CategoryRepository.class)
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("모든 카테고리를 조회한다")
    @Test
    public void find_all_category() {
        // given
        Category student = new Category(CategoryName.STUDENT);
        Category bachelor = new Category(CategoryName.BACHELOR);
        Category employment = new Category(CategoryName.EMPLOYMENT);
        Category library = new Category(CategoryName.EMPLOYMENT);
        Category department = new Category(CategoryName.DEPARTMENT);
        categoryRepository.saveAll(List.of(student, bachelor, employment, library, department));

        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        assertThat(categoryList).contains(student, bachelor, employment, library, department);
    }
}

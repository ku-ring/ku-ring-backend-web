package com.kustacks.kuring.config;

import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.category.CategoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

public class MappedBeanConfig {

    @Configuration
    public static class CategoryEntityNameMappedBeanConfig {

        private final CategoryRepository categoryRepository;

        public CategoryEntityNameMappedBeanConfig(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @Bean
        public Map<String, Category> categoryMap() {
            return categoryRepository.findAllMap();
        }
    }
}

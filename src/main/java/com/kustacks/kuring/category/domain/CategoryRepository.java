package com.kustacks.kuring.category.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.stream.Collectors;

public interface CategoryRepository extends JpaRepository<Category, CategoryName>, CategoryQueryRepository {
    default Map<String, Category> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Category::getName, v -> v));
    }
}

package com.kustacks.kuring.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.stream.Collectors;

public interface CategoryRepository extends JpaRepository<Category, String> {
    default Map<String, Category> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Category::getName, v -> v));
    }
}

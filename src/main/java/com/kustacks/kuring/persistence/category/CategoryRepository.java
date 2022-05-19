package com.kustacks.kuring.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    default Map<String, Category> findAllMap() {
        return findAll().stream().collect(Collectors.toMap(Category::getName, v -> v));
    }

    Optional<Category> findByName(String name);
    List<Category> findAllByParentName(String name);
}

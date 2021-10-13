package com.kustacks.kuring.domain.notice;

import com.kustacks.kuring.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCategory(Category category);

    default Map<String, Notice> findByCategoryMap(Category category) {
        return findByCategory(category).stream().collect(Collectors.toMap(Notice::getArticleId, v -> v));
    }
}

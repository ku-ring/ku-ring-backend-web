package com.kustacks.kuring.domain.notice;

import com.kustacks.kuring.domain.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByCategory(Category category);
    List<Notice> findByCategory(Category category, Pageable pageable);
    List<Notice> findBySubjectContainingOrCategoryNameContaining(String subject, String categoryName);

    default Map<String, Notice> findByCategoryMap(Category category) {
        return findByCategory(category).stream().collect(Collectors.toMap(Notice::getArticleId, v -> v));
    }
}

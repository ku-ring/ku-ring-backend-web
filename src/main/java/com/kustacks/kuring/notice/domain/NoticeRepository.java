package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQueryRepository {

    List<Notice> findByCategory(Category category);

    default Map<String, Notice> findNoticeMapByCategory(Category category) {
        return findByCategory(category).stream().collect(Collectors.toMap(Notice::getArticleId, v -> v));
    }
}

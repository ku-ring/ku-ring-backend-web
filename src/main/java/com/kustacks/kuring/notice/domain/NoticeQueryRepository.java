package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeQueryRepository {

    List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable);

    List<NoticeSearchDto> findAllByKeywords(List<String> containedNames);

    List<String> findNormalArticleIdsByCategory(CategoryName categoryName);

    void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds);
}

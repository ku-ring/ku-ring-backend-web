package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.notice.common.dto.response.NoticeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeQueryRepository {

    List<NoticeDto> findNoticesByCategoryWithOffset(Category category, Pageable pageable);
}

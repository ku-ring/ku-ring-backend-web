package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.user.common.dto.BookmarkDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserQueryRepository {

    List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable);
    List<BookmarkDto> findAllBookmarks(List<String> ids);
}

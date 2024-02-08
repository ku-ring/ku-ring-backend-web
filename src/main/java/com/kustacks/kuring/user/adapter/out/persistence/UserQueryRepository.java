package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

interface UserQueryRepository {

    List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable);
}

package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import com.kustacks.kuring.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

interface UserQueryRepository {

    Page<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable);

    List<User> findByPageRequest(Pageable pageable);

    void resetAllUserQuestionCount();
}

package com.kustacks.kuring.user.application.port.out;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserQueryPort {

    List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable);
    List<String> findAllToken();
    Optional<User> findByToken(String token);
    List<User> findAll();
}

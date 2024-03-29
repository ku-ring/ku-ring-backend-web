package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.user.application.port.out.dto.FeedbackDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminUserFeedbackPort {
    List<String> findAllToken();
    List<FeedbackDto> findAllFeedbackByPageRequest(Pageable pageable);
}

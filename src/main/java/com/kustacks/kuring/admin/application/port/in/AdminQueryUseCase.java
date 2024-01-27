package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;

import java.util.List;

public interface AdminQueryUseCase {
    List<AdminFeedbacksResult> lookupFeedbacks(int page, int size);
}

package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertResponse;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;

import java.util.List;

public interface AdminQueryUseCase {
    List<AdminFeedbacksResult> lookupFeedbacks(int page, int size);

    List<AdminAlertResponse> lookupAlerts(int page, int size);
}

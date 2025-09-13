package com.kustacks.kuring.admin.application.port.in;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertResponse;
import com.kustacks.kuring.report.application.port.in.dto.AdminReportsResult;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import org.springframework.data.domain.Page;


public interface AdminQueryUseCase {

    Page<AdminFeedbacksResult> lookupFeedbacks(int page, int size);

    Page<AdminAlertResponse> lookupAlerts(int page, int size);

    Page<AdminReportsResult> lookupReports(int page, int size);
}

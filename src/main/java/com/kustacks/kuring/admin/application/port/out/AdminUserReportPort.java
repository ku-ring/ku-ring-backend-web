package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.report.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserReportPort {
    Page<Report> findAllReportByPageRequest(Pageable pageable);
}

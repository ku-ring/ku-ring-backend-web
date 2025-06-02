package com.kustacks.kuring.report.adapter.out.persistence;

import com.kustacks.kuring.admin.application.port.out.AdminUserReportPort;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.report.application.port.out.ReportCommandPort;
import com.kustacks.kuring.report.domain.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@PersistenceAdapter
@RequiredArgsConstructor
class ReportPersistenceAdapter implements ReportCommandPort, AdminUserReportPort {

    private final ReportRepository reportRepository;

    @Override
    public void save(Report report) {
        reportRepository.save(report);
    }

    @Override
    public Page<Report> findAllReportByPageRequest(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }
}

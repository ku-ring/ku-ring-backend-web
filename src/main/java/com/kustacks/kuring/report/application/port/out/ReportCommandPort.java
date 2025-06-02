package com.kustacks.kuring.report.application.port.out;

import com.kustacks.kuring.report.domain.Report;

public interface ReportCommandPort {

    void save(Report report);
}

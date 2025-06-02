package com.kustacks.kuring.report.application.port.out;

import com.kustacks.kuring.report.domain.Report;
import com.kustacks.kuring.report.domain.ReportTargetType;
import com.kustacks.kuring.user.domain.User;

import java.util.Optional;

public interface ReportQueryPort {

    Optional<Report> findByReporterAndTargetIdAndType(User user, Long targetId, ReportTargetType type);
}

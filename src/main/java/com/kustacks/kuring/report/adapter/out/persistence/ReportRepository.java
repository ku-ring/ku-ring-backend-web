package com.kustacks.kuring.report.adapter.out.persistence;

import com.kustacks.kuring.report.domain.Report;
import com.kustacks.kuring.report.domain.ReportTargetType;
import com.kustacks.kuring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByReporterAndTargetIdAndTargetType(User reporter, Long targetId, ReportTargetType targetType);
}

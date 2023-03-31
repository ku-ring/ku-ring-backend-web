package com.kustacks.kuring.notice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentNoticeRepository extends JpaRepository<DepartmentNotice, Long>, DepartmentNoticeQueryRepository {
}

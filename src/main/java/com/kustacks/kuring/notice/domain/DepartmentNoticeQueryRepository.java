package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.worker.DepartmentName;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentNoticeQueryRepository {

    List<String> findArticleIdsByDepartmentWithLimit(DepartmentName departmentName, int limit);

    List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName);

    List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable);
}

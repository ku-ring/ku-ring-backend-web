package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.worker.DepartmentName;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentNoticeQueryRepository {

    List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName);

    List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable);

    List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentNameEnum);

    List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentNameEnum);

    void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds);
}

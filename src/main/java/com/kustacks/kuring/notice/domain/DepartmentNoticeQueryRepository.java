package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.worker.DepartmentName;

import java.util.List;

public interface DepartmentNoticeQueryRepository {

    List<String> findTopTwentyFourArticleIdByDepartment(DepartmentName departmentName);
}

package com.kustacks.kuring.notice.application.port.in.dto;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;

public record NoticeDepartmentNameResult(
        String name,
        String hostPrefix,
        String korName,
        boolean graduateSupported
) {
    public static NoticeDepartmentNameResult from(DeptInfo deptInfo) {
        DepartmentName name = deptInfo.getDepartmentName();
        return new NoticeDepartmentNameResult(name.getName(), name.getHostPrefix(), name.getKorName(), deptInfo.isSupportGraduateScrap());
    }
}

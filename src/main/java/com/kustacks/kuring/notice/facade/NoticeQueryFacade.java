package com.kustacks.kuring.notice.facade;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.CategoryNameDto;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeLookupResponse;
import com.kustacks.kuring.notice.common.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.DepartmentName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeQueryFacade {

    private final NoticeService noticeService;
    private final CategoryService categoryService;

    public List<NoticeDto> getNotices(String type, String department, Boolean important, int page, int size) {
        return noticeService.getNoticesV2(type, department, important, page, size);
    }

    public NoticeLookupResponse searchNoticeByContent(String content) {
        List<NoticeSearchDto> noticeDtoList = noticeService.findAllNoticeByContent(content);
        return new NoticeLookupResponse(noticeDtoList);
    }

    public List<CategoryNameDto> getSupportedCategories() {
        return categoryService.lookUpSupportedCategories()
                .stream()
                .map(CategoryNameDto::from)
                .collect(Collectors.toList());
    }

    public List<DepartmentNameDto> getSupportedDepartments() {
        List<DepartmentName> departmentNames = noticeService.lookupSupportedDepartments();
        return convertDepartmentNameDtos(departmentNames);
    }

    private List<DepartmentNameDto> convertDepartmentNameDtos(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .filter(dn -> !dn.equals(DepartmentName.BIO_SCIENCE))
                .filter(dn -> !dn.equals(DepartmentName.COMM_DESIGN))
                .map(DepartmentNameDto::from)
                .collect(Collectors.toList());
    }
}

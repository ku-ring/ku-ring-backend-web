package com.kustacks.kuring.notice.business;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.notice.common.OffsetBasedPageRequest;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeListResponse;
import com.kustacks.kuring.notice.common.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final CategoryName[] supportedCategoryNameList;
    private final DepartmentName[] supportedDepartmentNameList;
    private final String SPACE_REGEX = "[\\s+]";

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public NoticeService(NoticeRepository noticeRepository, DepartmentNoticeRepository departmentNoticeRepository) {
        this.noticeRepository = noticeRepository;
        this.departmentNoticeRepository = departmentNoticeRepository;
        this.supportedCategoryNameList = CategoryName.values();
        this.supportedDepartmentNameList = DepartmentName.values();
    }

    public List<DepartmentName> lookupSupportedDepartments() {
        return List.of(supportedDepartmentNameList);
    }

    public NoticeListResponse getNotices(String type, int offset, int max) {
        String categoryName = convertShortNameIntoLongName(type);

        List<NoticeDto> noticeDtoList = noticeRepository
                .findNoticesByCategoryWithOffset(CategoryName.fromStringName(categoryName), new OffsetBasedPageRequest(offset, max));

        return new NoticeListResponse(convertBaseUrl(categoryName), noticeDtoList);
    }

    public List<NoticeDto> getNoticesV2(String type, String department, Boolean important, int page, int size) {
        if (isDepartmentSearchRequest(type, department)) {
            DepartmentName departmentName = DepartmentName.fromHostPrefix(department);

            if (Boolean.TRUE.equals(important)) {
                return departmentNoticeRepository.findImportantNoticesByDepartment(departmentName);
            } else {
                return departmentNoticeRepository.findNormalNoticesByDepartmentWithOffset(departmentName, PageRequest.of(page, size));
            }
        }

        String categoryName = convertShortNameIntoLongName(type);
        if (isDepartment(categoryName)) {
            throw new InternalLogicException(ErrorCode.API_INVALID_PARAM);
        }

        return noticeRepository.findNoticesByCategoryWithOffset(CategoryName.fromStringName(categoryName), PageRequest.of(page, size));
    }

    public List<NoticeSearchDto> findAllNoticeByContent(String content) {
        String[] splitedKeywords = splitBySpace(content);

        List<String> keywords = noticeCategoryNameConvertEnglish(splitedKeywords);

        return noticeRepository.findAllByKeywords(keywords);
    }

    private boolean isDepartmentSearchRequest(String type, String department) {
        return type.equals("dep") && !department.isEmpty();
    }

    private boolean isDepartment(String categoryName) {
        return CategoryName.DEPARTMENT.isSameName(categoryName);
    }

    private String[] splitBySpace(String content) {
        return content.trim().split(SPACE_REGEX);
    }

    private List<String> noticeCategoryNameConvertEnglish(String[] splitedKeywords) {
        return Arrays.stream(splitedKeywords)
                .map(this::convertEnglish)
                .collect(Collectors.toList());
    }

    private String convertEnglish(String keyword) {
        for (CategoryName categoryName : supportedCategoryNameList) {
            if (categoryName.isSameKorName(keyword)) {
                return categoryName.getName();
            }
        }
        return keyword;
    }

    private String convertShortNameIntoLongName(String typeShortName) {
        return Arrays.stream(supportedCategoryNameList)
                .filter(categoryName -> categoryName.isSameShortName(typeShortName))
                .findFirst()
                .map(CategoryName::getName)
                .orElseThrow(() -> new NotFoundException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY));
    }

    private String convertBaseUrl(String categoryName) {
        return CategoryName.LIBRARY.isSameName(categoryName) ? libraryBaseUrl : normalBaseUrl;
    }
}

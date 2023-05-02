package com.kustacks.kuring.notice.business;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.category.exception.CategoryNotFoundException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.notice.common.OffsetBasedPageRequest;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeListResponse;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.search.common.dto.NoticeSearchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private static final int FIRST_PAGE = 0;

    private final NoticeRepository noticeRepository;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final Map<String, Category> categoryMap;
    private final CategoryName[] categoryNames;
    private final List<DepartmentNameDto> supportedDepartmentNameList;
    private final String SPACE_REGEX = "[\\s+]";

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public NoticeService(NoticeRepository noticeRepository, DepartmentNoticeRepository departmentNoticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.departmentNoticeRepository = departmentNoticeRepository;
        this.categoryMap = categoryRepository.findAllMap();
        this.categoryNames = CategoryName.values();
        this.supportedDepartmentNameList = supportedDepartmentNameList();
    }

    public List<DepartmentNameDto> lookupSupportedDepartments() {
        return supportedDepartmentNameList;
    }

    public NoticeListResponse getNotices(String type, int offset, int max) {
        String categoryName = convertShortNameIntoLongName(type);
        Category category = getCategoryByName(categoryName);

        List<NoticeDto> noticeDtoList = noticeRepository.findNoticesByCategoryWithOffset(category, new OffsetBasedPageRequest(offset, max));

        return new NoticeListResponse(convertBaseUrl(categoryName), noticeDtoList);
    }

    public List<NoticeDto> getNoticesV2(String type, String department, Boolean important, int page, int size) {
        if (isDepartmentSearchRequest(type, department)) {
            DepartmentName departmentName = DepartmentName.fromHostPrefix(department);

            if(Boolean.TRUE.equals(important)) {
                return departmentNoticeRepository.findImportantNoticesByDepartment(departmentName);
            } else {
                return departmentNoticeRepository.findNormalNoticesByDepartmentWithOffset(departmentName, PageRequest.of(page, size));
            }
        }

        String categoryName = convertShortNameIntoLongName(type);
        if (isDepartment(categoryName)) {
            throw new InternalLogicException(ErrorCode.API_INVALID_PARAM);
        }

        Category category = getCategoryByName(categoryName);

        return noticeRepository.findNoticesByCategoryWithOffset(category, PageRequest.of(page, size));
    }

    public List<NoticeSearchDto> findAllNoticeByContent(String content) {
        String[] splitedKeywords = splitBySpace(content);

        List<String> keywords = noticeCategoryNameConvertEnglish(splitedKeywords);

        return noticeRepository.findAllByKeywords(keywords);
    }

    public List<NoticeSearchDto> handleSearchRequest(String keywords) {
        String[] splitedKeywords = keywords.trim().split(SPACE_REGEX);

        // 키워드 중 공지 카테고리가 있다면, 이를 영문으로 변환
        for (int i = 0; i < splitedKeywords.length; ++i) {
            for (CategoryName categoryName : categoryNames) {
                if (splitedKeywords[i].equals(categoryName.getKorName())) {
                    splitedKeywords[i] = categoryName.getName();
                    break;
                }
            }
        }
        return getNoticesBySubjectOrCategory(splitedKeywords);
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
        for (CategoryName categoryName : categoryNames) {
            if (categoryName.isSameKorName(keyword)) {
                return categoryName.getName();
            }
        }
        return keyword;
    }

    private Category getCategoryByName(String categoryName) {
        Category category = categoryMap.get(categoryName);
        if (category == null) {
            throw new CategoryNotFoundException();
        }
        return category;
    }

    private String convertShortNameIntoLongName(String typeShortName) {
        return Arrays.stream(categoryNames)
                .filter(categoryName -> categoryName.isSameShortName(typeShortName))
                .findFirst()
                .map(CategoryName::getName)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private String convertBaseUrl(String categoryName) {
        return CategoryName.LIBRARY.isSameName(categoryName) ? libraryBaseUrl : normalBaseUrl;
    }

    private List<NoticeSearchDto> getNoticesBySubjectOrCategory(String[] keywords) {
        return noticeRepository.findAllByKeywords(Arrays.asList(keywords));
    }

    private List<DepartmentNameDto> supportedDepartmentNameList() {
        return Arrays.stream(DepartmentName.values())
                .filter(dn -> !dn.equals(DepartmentName.BIO_SCIENCE))
                .filter(dn -> !dn.equals(DepartmentName.COMM_DESIGN))
                .map(DepartmentNameDto::from)
                .collect(Collectors.toList());
    }
}

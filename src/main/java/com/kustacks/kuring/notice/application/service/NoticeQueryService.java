package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.in.NoticeQueryUseCase;
import com.kustacks.kuring.notice.application.port.in.dto.*;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.kustacks.kuring.notice.domain.CategoryName.DEPARTMENT;

@UseCase
@Transactional(readOnly = true)
public class NoticeQueryService implements NoticeQueryUseCase {

    private static final String SPACE_REGEX = "[\\s+]";
    private final NoticeQueryPort noticeQueryPort;
    private final List<CategoryName> supportedCategoryNameList;
    private final List<DepartmentName> supportedDepartmentNameList;

    public NoticeQueryService(NoticeQueryPort noticeQueryPort) {
        this.noticeQueryPort = noticeQueryPort;
        this.supportedCategoryNameList = Arrays.asList(CategoryName.values());
        this.supportedDepartmentNameList = Arrays.asList(DepartmentName.values());
    }

    @Override
    public List<NoticeRangeLookupResult> getNotices(NoticeRangeLookupCommand command) {
        if (isDepartmentSearchRequest(command.type(), command.department())) {
            return getDepartmentNoticeRangeLookup(command);
        }

        return getNoticeRangeLookup(command);
    }

    @Override
    public List<NoticeContentSearchResult> findAllNoticeByContent(String content) {
        String[] splitedKeywords = splitBySpace(content);
        List<String> keywords = noticeCategoryNameConvertEnglish(splitedKeywords);
        return searchNoticesByKeywords(keywords);
    }

    @Override
    public List<NoticeCategoryNameResult> lookupSupportedCategories() {
        return supportedCategoryNameList.stream()
                .map(NoticeCategoryNameResult::from)
                .toList();
    }

    @Override
    public List<NoticeDepartmentNameResult> lookupSupportedDepartments() {
        return convertDepartmentNameDtos(supportedDepartmentNameList);
    }

    private List<NoticeContentSearchResult> searchNoticesByKeywords(List<String> keywords) {
        final String SPACE = " ";
        final int DATE_INDEX = 0;

        return noticeQueryPort.findAllByKeywords(keywords)
                .stream()
                .map(dto -> new NoticeContentSearchResult(
                        dto.getArticleId(),
                        dto.getPostedDate().split(SPACE)[DATE_INDEX],
                        dto.getSubject(),
                        dto.getCategoryName(),
                        dto.getBaseUrl()
                ))
                .toList();
    }

    private List<NoticeRangeLookupResult> getNoticeRangeLookup(NoticeRangeLookupCommand command) {
        String categoryName = convertShortNameIntoLongName(command.type());
        if (isDepartment(categoryName)) {
            throw new InternalLogicException(ErrorCode.API_INVALID_PARAM);
        }

        return noticeQueryPort
                .findNoticesByCategoryWithOffset(
                        CategoryName.fromStringName(categoryName),
                        PageRequest.of(command.page(), command.size())
                ).stream()
                .map(NoticeQueryService::convertPortResult)
                .toList();
    }

    private List<NoticeRangeLookupResult> getDepartmentNoticeRangeLookup(NoticeRangeLookupCommand command) {
        DepartmentName departmentName = DepartmentName.fromHostPrefix(command.department());

        if (command.isImportant()) {
            return noticeQueryPort
                    .findImportantNoticesByDepartment(departmentName)
                    .stream()
                    .map(NoticeQueryService::convertPortResult)
                    .toList();
        }

        return noticeQueryPort
                .findNormalNoticesByDepartmentWithOffset(
                        departmentName,
                        PageRequest.of(command.page(), command.size())
                ).stream()
                .map(NoticeQueryService::convertPortResult)
                .toList();
    }

    private List<NoticeDepartmentNameResult> convertDepartmentNameDtos(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .filter(dn -> !dn.equals(DepartmentName.COMM_DESIGN))
                .map(NoticeDepartmentNameResult::from)
                .toList();
    }

    private boolean isDepartmentSearchRequest(String type, String department) {
        return type.equals("dep") && !department.isEmpty();
    }

    private boolean isDepartment(String categoryName) {
        return DEPARTMENT.isSameName(categoryName);
    }

    private String[] splitBySpace(String content) {
        return content.trim().split(SPACE_REGEX);
    }

    private List<String> noticeCategoryNameConvertEnglish(String[] splitedKeywords) {
        return Arrays.stream(splitedKeywords)
                .map(this::convertEnglish)
                .toList();
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
        return supportedCategoryNameList.stream()
                .filter(categoryName -> categoryName.isSameShortName(typeShortName))
                .findFirst()
                .map(CategoryName::getName)
                .orElseThrow(() -> new NotFoundException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY));
    }

    public static NoticeRangeLookupResult convertPortResult(NoticeDto dto) {
        return new NoticeRangeLookupResult(
                dto.getArticleId(),
                dto.getPostedDate(),
                dto.getUrl(),
                dto.getSubject(),
                dto.getCategory(),
                dto.getImportant()
        );
    }
}

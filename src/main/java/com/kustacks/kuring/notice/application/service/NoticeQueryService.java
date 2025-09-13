package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.data.Cursor;
import com.kustacks.kuring.common.data.CursorBasedList;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.adapter.in.web.dto.CommentDetailResponse;
import com.kustacks.kuring.notice.application.port.in.NoticeCommentReadingUseCase;
import com.kustacks.kuring.notice.application.port.in.NoticeQueryUseCase;
import com.kustacks.kuring.notice.application.port.in.dto.*;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import com.kustacks.kuring.user.domain.RootUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.kustacks.kuring.notice.domain.CategoryName.DEPARTMENT;

@UseCase
@Transactional(readOnly = true)
public class NoticeQueryService implements NoticeQueryUseCase, NoticeCommentReadingUseCase {

    private static final int MAX_COMMENT_QUERY_SIZE = 30;
    private static final int START_INDEX = 0;

    private static final String SPACE_REGEX = "[\\s+]";
    private final NoticeQueryPort noticeQueryPort;
    private final CommentQueryPort commentQueryPort;
    private final RootUserQueryPort rootUserQueryPort;
    private final List<CategoryName> supportedCategoryNameList;
    private final List<DepartmentName> supportedDepartmentNameList;

    public NoticeQueryService(
            NoticeQueryPort noticeQueryPort,
            CommentQueryPort commentQueryPort,
            RootUserQueryPort rootUserQueryPort
    ) {
        this.noticeQueryPort = noticeQueryPort;
        this.commentQueryPort = commentQueryPort;
        this.rootUserQueryPort = rootUserQueryPort;
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

    @Override
    public CursorBasedList<CommentAndSubCommentsResult> findComments(Long noticeId, Cursor cursor, int size, String email) {
        Optional<RootUser> optionalRootUser = rootUserQueryPort.findRootUserByEmail(email);
        Long currentUserId = optionalRootUser.map(RootUser::getId).orElse(null);

        return CursorBasedList.of(
                Math.min(size, MAX_COMMENT_QUERY_SIZE),
                it -> it.comment().id().toString(),
                searchSize -> {
                    List<CommentReadModel> parentComments = commentQueryPort
                            .findExcludeSubCommentByCursor(noticeId, cursor.getStringCursor(), searchSize);

                    Set<Long> parentCommentIds = parentComments.stream()
                            .map(CommentReadModel::getId)
                            .collect(Collectors.toSet());

                    List<CommentReadModel> subComments = commentQueryPort.findSubCommentByIds(noticeId, parentCommentIds);

                    // 부모 댓글을 Key로 하고, 그에 해당하는 자식 댓글 리스트를 값으로 가지는 Map 생성
                    Map<Long, List<CommentDetailResponse>> parentToSubCommentsMap = subComments.stream()
                            .filter(subComment -> subComment.getParentId() != null)
                            .map(subComment -> CommentDetailResponse.of(subComment, currentUserId))
                            .collect(Collectors.groupingBy(CommentDetailResponse::parentId));

                    // 부모 댓글과 자식 댓글을 조합하여 CommentAndSubCommentsResult 리스트 생성
                    List<CommentAndSubCommentsResult> commentResults = parentComments.stream()
                            .map(parent -> {
                                List<CommentDetailResponse> subCommentList = parentToSubCommentsMap
                                        .getOrDefault(parent.getId(), Collections.emptyList());

                                // 부모 댓글이 삭제되었고, 답글이 없는 경우 제외
                                if (parent.getDestroyedAt() != null && subCommentList.isEmpty()) {
                                    return null;
                                }

                                return new CommentAndSubCommentsResult(CommentDetailResponse.of(parent, currentUserId), subCommentList);
                            })
                            .filter(Objects::nonNull)
                            .toList();

                    return commentResults.subList(START_INDEX, Math.min(searchSize, commentResults.size()));
                }
        );
    }

    private List<NoticeContentSearchResult> searchNoticesByKeywords(List<String> keywords) {
        final String SPACE = " ";
        final int DATE_INDEX = 0;

        return noticeQueryPort.findAllByKeywords(keywords)
                .stream()
                .map(dto -> new NoticeContentSearchResult(
                        dto.getId(),
                        dto.getArticleId(),
                        dto.getPostedDate().split(SPACE)[DATE_INDEX],
                        dto.getSubject(),
                        dto.getCategoryName(),
                        dto.getBaseUrl(),
                        dto.getCommentCount()
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

        Boolean graduated = (command.graduated() == null) ? Boolean.FALSE : command.graduated();

        if (command.isImportant()) {
            return noticeQueryPort
                    .findImportantNoticesByDepartment(departmentName, graduated)
                    .stream()
                    .map(NoticeQueryService::convertPortResult)
                    .toList();
        }

        return noticeQueryPort
                .findNormalNoticesByDepartmentWithOffset(
                        departmentName,
                        graduated,
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

    private static NoticeRangeLookupResult convertPortResult(NoticeDto dto) {
        return new NoticeRangeLookupResult(
                dto.getId(),
                dto.getArticleId(),
                dto.getPostedDate(),
                dto.getUrl(),
                dto.getSubject(),
                dto.getCategory(),
                dto.getImportant(),
                dto.getGraduated(),
                dto.getCommentCount()
        );
    }
}

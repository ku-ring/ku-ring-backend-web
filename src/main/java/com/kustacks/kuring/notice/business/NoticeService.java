package com.kustacks.kuring.notice.business;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.common.OffsetBasedPageRequest;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.utils.ObjectComparator;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.notice.common.dto.response.NoticeDto;
import com.kustacks.kuring.notice.common.dto.response.NoticeListResponse;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final Map<String, Category> categoryMap;
    private final CategoryName[] categoryNames;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public NoticeService(NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMap = categoryRepository.findAllMap();
        this.categoryNames = CategoryName.values();
    }

    public NoticeListResponse getNotices(String type, int offset, int max) {
        String categoryName = convertShortNameIntoLongName(type);

        Category category = getCategoryByName(categoryName);
        List<Notice> notices = noticeRepository.findByCategory(category,
                new OffsetBasedPageRequest(offset, max, Sort.by(Sort.Direction.DESC, "postedDate")));

        return new NoticeListResponse(convertBaseUrl(categoryName), noticeEntityToDTO(notices));
    }

    public List<Notice> handleSearchRequest(String keywords) {

        keywords = keywords.trim();
        String[] splitedKeywords = keywords.split("[\\s+]");

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

    private Category getCategoryByName(String categoryName) {
        Category category = categoryMap.get(categoryName);
        if (category == null) {
            throw new APIException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY);
        }
        return category;
    }

    private String convertShortNameIntoLongName(String typeShortName) {
        return Arrays.stream(categoryNames)
                .filter(categoryName -> categoryName.isSameShortName(typeShortName))
                .findFirst()
                .map(CategoryName::getName)
                .orElseThrow(() -> new APIException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY));
    }

    private String convertBaseUrl(String type) {
        return CategoryName.LIBRARY.isSameShortName(type) ? libraryBaseUrl : normalBaseUrl;
    }

    private List<Notice> getNoticesBySubjectOrCategory(String[] keywords) {

        List<Notice> notices = noticeRepository.findBySubjectContainingOrCategoryNameContaining(keywords[0], keywords[0]);
        Iterator<Notice> iterator = notices.iterator();

        for (int i = 1; i < keywords.length; ++i) {
            while (iterator.hasNext()) {
                Notice notice = iterator.next();
                String curKeyword = keywords[i];

                if (notice.getSubject().contains(curKeyword) || notice.getCategory().getName().contains(curKeyword)) {

                } else {
                    iterator.remove();
                }
            }
        }

        // 날짜 내림차순 정렬
        notices.sort(ObjectComparator.NoticeDateComparator);

        return notices;
    }

    // TODO: noticeDTO 클래스에 위치하는게 맞는듯?
    private List<NoticeDto> noticeEntityToDTO(List<Notice> notices) {
        List<NoticeDto> noticeDtoList = new ArrayList<>(notices.size());
        for (Notice notice : notices) {
            noticeDtoList.add(NoticeDto.builder()
                    .articleId(notice.getArticleId())
                    .postedDate(notice.getPostedDate())
                    .subject(notice.getSubject())
                    .category(notice.getCategory().getName())
                    .build());
        }

        return noticeDtoList;
    }
}

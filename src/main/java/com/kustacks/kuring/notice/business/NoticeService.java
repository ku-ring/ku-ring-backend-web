package com.kustacks.kuring.notice.business;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.category.exception.CategoryNotFoundException;
import com.kustacks.kuring.common.utils.ObjectComparator;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.notice.common.dto.response.NoticeDto;
import com.kustacks.kuring.notice.common.dto.response.NoticeListResponse;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final Map<String, Category> categoryMap;
    private final CategoryName[] categoryNames;

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    public NoticeService(NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryMap = categoryRepository.findAllMap();
        this.categoryNames = CategoryName.values();
    }

    public NoticeListResponse getNotices(String type, int offset, int max) {
        String categoryName = convertShortNameIntoLongName(type);
        Category category = getCategoryByName(categoryName);
        List<NoticeDto> noticeDtoList = noticeRepository.findNoticesByCategoryWithOffset(category, PageRequest.of(offset, max));
        return new NoticeListResponse(convertBaseUrl(categoryName), noticeDtoList);
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
}

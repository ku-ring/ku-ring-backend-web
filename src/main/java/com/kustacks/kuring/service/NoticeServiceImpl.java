package com.kustacks.kuring.service;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.persistence.OffsetBasedPageRequest;
import com.kustacks.kuring.persistence.category.Category;
import com.kustacks.kuring.persistence.category.CategoryRepository;
import com.kustacks.kuring.persistence.notice.Notice;
import com.kustacks.kuring.persistence.notice.NoticeRepository;
import com.kustacks.kuring.CategoryName;
import com.kustacks.kuring.service.utils.ObjectComparator;
import com.kustacks.kuring.util.converter.DTOConverter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final Map<String, Category> categoryMap;
    private final DTOConverter<NoticeDTO, Notice> noticeEntityToNoticeDTOConverter;
    private final CategoryName[] categoryNames;

    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             DTOConverter<NoticeDTO, Notice> noticeEntityToNoticeDTOConverter,
                             Map<String, Category> categoryMap
    ) {
        this.noticeRepository = noticeRepository;
        this.noticeEntityToNoticeDTOConverter = noticeEntityToNoticeDTOConverter;
        this.categoryMap = categoryMap;
        this.categoryNames = CategoryName.values();
    }

    public List<NoticeDTO> getNotices(String type, int offset, int max) {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, max, Sort.by(Sort.Direction.DESC, "postedDate", "id"));

        Category category = categoryMap.get(type);
        if(category == null) {
            return null;
        }

        List<Notice> notices = noticeRepository.findByCategory(category, pageRequest);

        List<NoticeDTO> noticeDTOList = new LinkedList<>();
        for (Notice notice : notices) {
            noticeDTOList.add(noticeEntityToNoticeDTOConverter.convert(notice));
        }
        return noticeDTOList;
    }

    public List<Notice> handleSearchRequest(String keywords) {

        keywords = keywords.trim();
        String[] splitedKeywords = keywords.split("[\\s+]");
        
        // 키워드 중 공지 카테고리가 있다면, 이를 영문으로 변환
        for (int i=0; i<splitedKeywords.length; ++i) {
            for (CategoryName categoryName : categoryNames) {
                if(splitedKeywords[i].equals(categoryName.getKorName())) {
                    splitedKeywords[i] = categoryName.getName();
                    break;
                }
            }
        }

        return getNoticesBySubjectOrCategory(splitedKeywords);
    }

    private List<Notice> getNoticesBySubjectOrCategory(String[] keywords) {

        List<Notice> notices = noticeRepository.findBySubjectContainingOrCategoryNameContaining(keywords[0], keywords[0]);
        Iterator<Notice> iterator = notices.iterator();

        for(int i=1; i<keywords.length; ++i) {
            while(iterator.hasNext()) {
                Notice notice = iterator.next();
                String curKeyword = keywords[i];

                if(notice.getSubject().contains(curKeyword) || notice.getCategory().getName().contains(curKeyword)) {

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

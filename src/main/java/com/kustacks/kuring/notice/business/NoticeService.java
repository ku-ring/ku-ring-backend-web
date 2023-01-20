package com.kustacks.kuring.notice.business;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.common.OffsetBasedPageRequest;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.common.utils.ObjectComparator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final Map<String, Category> categoryMap;
    private final CategoryName[] categoryNames;

    public NoticeService(NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;

        categoryMap = categoryRepository.findAllMap();
        categoryNames = CategoryName.values();
    }

    public List<NoticeDTO> getNotices(String type, int offset, int max) {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(offset, max, Sort.by(Sort.Direction.DESC, "postedDate"));
//        PageRequest pageRequest = PageRequest.of(offset / max, max);

        Category category = categoryMap.get(type);
        if(category == null) {
            return null;
        }

        List<Notice> notices = noticeRepository.findByCategory(category, pageRequest);

        return noticeEntityToDTO(notices);
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



    // TODO: noticeDTO 클래스에 위치하는게 맞는듯?
    private List<NoticeDTO> noticeEntityToDTO(List<Notice> notices) {
        List<NoticeDTO> noticeDTOList = new ArrayList<>(notices.size());
        for (Notice notice : notices) {
            noticeDTOList.add(NoticeDTO.builder()
                    .articleId(notice.getArticleId())
                    .postedDate(notice.getPostedDate())
                    .subject(notice.getSubject())
                    .categoryName(notice.getCategory().getName())
                    .build());
        }

        return noticeDTOList;
    }
}

package com.kustacks.kuring.service;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.domain.OffsetBasedPageRequest;
import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final Map<String, Category> categoryMap;

    public NoticeService(NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;

        categoryMap = categoryRepository.findAllMap();
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

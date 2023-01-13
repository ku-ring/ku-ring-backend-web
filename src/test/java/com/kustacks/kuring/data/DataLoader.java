package com.kustacks.kuring.data;

import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.category.CategoryRepository;
import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.notice.NoticeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;

    public DataLoader(NoticeRepository noticeRepository, CategoryRepository categoryRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * @version : 1.0.0
     * DB를 사용하는 테스트에서 초기화 데이터 삽입용 메서드
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     */
    @Transactional
    public void loadData() {
        log.info("[call DataLoader]");

        Category student = new Category("student");
        Category bachelor = new Category("bachelor");
        Category employment = new Category("employment");
        categoryRepository.saveAll(List.of(student, bachelor, employment));

        List<Notice> noticeList = buildNotices(5, student);
        noticeRepository.saveAll(noticeList);

        log.info("[init complete DataLoader]");
    }

    private static List<Notice> buildNotices(int cnt, Category category) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Notice("article_" + i, "post_date_" + i, "update_date_" + i, "subject_" + i, category))
                .collect(Collectors.toList());
    }
}

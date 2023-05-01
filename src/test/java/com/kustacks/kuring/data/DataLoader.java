package com.kustacks.kuring.data;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.staff.domain.StaffRepository;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.notice.domain.DepartmentName;
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
    protected final String USER_FCM_TOKEN = "test_fcm_token";

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final DepartmentNoticeRepository departmentNoticeRepository;

    public DataLoader(NoticeRepository noticeRepository, CategoryRepository categoryRepository, UserRepository userRepository, StaffRepository staffRepository, DepartmentNoticeRepository departmentNoticeRepository) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.departmentNoticeRepository = departmentNoticeRepository;
    }

    /**
     * @version : 1.0.0
     * DB를 사용하는 테스트에서 초기화 데이터 삽입용 메서드
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     */
    @Transactional
    public void loadData() {
        log.info("[call DataLoader]");

        User newUser = new User(USER_FCM_TOKEN);
        userRepository.save(newUser);

        Category student = new Category(CategoryName.STUDENT);
        Category bachelor = new Category(CategoryName.BACHELOR);
        Category employment = new Category(CategoryName.EMPLOYMENT);
        Category library = new Category(CategoryName.LIBRARY);
        Category department = new Category(CategoryName.DEPARTMENT);
        categoryRepository.saveAll(List.of(student, bachelor, employment, library, department));

        List<Notice> noticeList = buildNotices(5, student);
        noticeRepository.saveAll(noticeList);

        List<Staff> staffList = buildStaffs(5);
        staffRepository.saveAll(staffList);

        List<DepartmentNotice> importantDeptNotices = buildDepartmentNotice(7, DepartmentName.COMPUTER, department, true);
        departmentNoticeRepository.saveAll(importantDeptNotices);

        List<DepartmentNotice> normalDeptNotices = buildDepartmentNotice(5, DepartmentName.COMPUTER, department, false);
        departmentNoticeRepository.saveAll(normalDeptNotices);

        log.info("[init complete DataLoader]");
    }

    private List<DepartmentNotice> buildDepartmentNotice(int cnt, DepartmentName departmentName, Category category, boolean important) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new DepartmentNotice("article_" + i, "post_date_" + i, "update_date_" + i, "subject_" + i, category, important, "https://www.example.com", departmentName))
                .collect(Collectors.toList());
    }

    private static List<Notice> buildNotices(int cnt, Category category) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Notice("article_" + i, "post_date_" + i, "update_date_" + i, "subject_" + i, category, false, "https://www.example.com"))
                .collect(Collectors.toList());
    }

    private static List<Staff> buildStaffs(int cnt) {
        return Stream.iterate(0, i -> i + 1)
                .limit(cnt)
                .map(i -> new Staff("shine_" + i, "computer", "lab", "phone", "email@naver.com", "dept", "college"))
                .collect(Collectors.toList());
    }
}

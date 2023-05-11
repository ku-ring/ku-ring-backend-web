package com.kustacks.kuring.tool;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.DepartmentNoticeRepository;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.notice.domain.NoticeRepository;
import com.kustacks.kuring.staff.domain.Staff;
import com.kustacks.kuring.staff.domain.StaffRepository;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DatabaseConfigurator implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigurator.class);
    protected final String USER_FCM_TOKEN = "test_fcm_token";

    private final NoticeRepository noticeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final DepartmentNoticeRepository departmentNoticeRepository;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    private List<String> tableNames;
    private Category student;
    private Category bachelor;
    private Category employment;
    private Category library;
    private Category department;

    public DatabaseConfigurator(NoticeRepository noticeRepository, CategoryRepository categoryRepository, UserRepository userRepository,
                                StaffRepository staffRepository, DepartmentNoticeRepository departmentNoticeRepository,
                                DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.noticeRepository = noticeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.departmentNoticeRepository = departmentNoticeRepository;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        extractTableNames();
        createFullTextIndex();
        log.info("[DatabaseConfigurator] afterPropertiesSet complete");
    }

    /**
     * @version : 1.1.0
     * 각 테스트 수행직후 저장된 데이터를 전부 삭제하는 메서드
     * 테스트 케이스별 독립환경 유지를 위한 용도
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     * @see : DatabaseCleanup#extractTableNames() 를 사용하여 truncate할 테이블 목록을 불러온다
     */
    public void clear() {
        truncateAllTable();
    }

    /**
     * @version : 1.1.0
     * DB를 사용하는 테스트에서 초기화 데이터 삽입용 메서드
     * @author : zbqmgldjfh(https://github.com/zbqmgldjfh)
     */
    @Transactional
    public void loadData() {
        log.info("[DatabaseConfigurator] init start");

        initUser();
        initCategory();
        initStaff();
        initNotice();

        log.info("[DatabaseConfigurator] init complete");
    }

    private void truncateAllTable() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = FALSE");
        for (String tableName : tableNames) {
            jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = TRUE");
    }

    private void extractTableNames() {
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                this.tableNames.add(tableName);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void createFullTextIndex() {
        jdbcTemplate.execute("CREATE FULLTEXT INDEX idx_notice_subject ON notice (subject)");
    }

    private void initUser() {
        User newUser = new User(USER_FCM_TOKEN);
        userRepository.save(newUser);
    }

    private void initNotice() {
        List<Notice> noticeList = buildNotices(5, student);
        noticeRepository.saveAll(noticeList);

        List<DepartmentNotice> importantDeptNotices = buildDepartmentNotice(7, DepartmentName.COMPUTER, department, true);
        departmentNoticeRepository.saveAll(importantDeptNotices);

        List<DepartmentNotice> normalDeptNotices = buildDepartmentNotice(5, DepartmentName.COMPUTER, department, false);
        departmentNoticeRepository.saveAll(normalDeptNotices);
    }

    private void initStaff() {
        List<Staff> staffList = buildStaffs(5);
        staffRepository.saveAll(staffList);
    }

    private void initCategory() {
        student = new Category(CategoryName.STUDENT);
        bachelor = new Category(CategoryName.BACHELOR);
        employment = new Category(CategoryName.EMPLOYMENT);
        library = new Category(CategoryName.LIBRARY);
        department = new Category(CategoryName.DEPARTMENT);
        categoryRepository.saveAll(List.of(student, bachelor, employment, library, department));
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
